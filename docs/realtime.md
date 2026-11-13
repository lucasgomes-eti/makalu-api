# Realtime: the Emitter broker

Order events reach the store web app and the customer Android app over MQTT, through a
self-hosted [Emitter](https://github.com/emitter-io/emitter) broker (AGPL-3.0).

Postgres and the REST API stay the only source of truth. Emitter delivers at QoS 0 and
keeps no session for an offline client, so a message is only ever a hint: clients refetch
over REST every time their connection is (re)established.

## What is deployed

Railway project `makalu`, environment `development`.

| Service | Source | Notes |
| --- | --- | --- |
| `emitter` | Docker image `emitter/server:v3.1` | One node, no clustering, in-memory storage, no volume |
| `api` | this repository | Publishes events and issues channel keys |

### `emitter`

- Listens on port `8080`. The same port serves raw MQTT over TCP and MQTT over
  WebSocket (the upgrade is accepted on any path).
- Public domain `emitter-development.up.railway.app` targets port 8080. Railway
  terminates TLS, so browsers and phones connect to `wss://emitter-development.up.railway.app/`.
- Private name `emitter.railway.internal`. The API connects to it by plain TCP on 8080
  and never goes through the public domain.
- Healthcheck `GET /health`; restart policy "always".

| Variable | Secret | Purpose |
| --- | --- | --- |
| `EMITTER_LICENSE` | yes | The broker's license. It contains the key every channel key is encrypted with. |
| `EMITTER_LISTEN` | no | Listen address, `:8080`. |

### `api`

| Variable | Secret | Purpose |
| --- | --- | --- |
| `REALTIME_ENABLED` | no | `true` turns realtime on. Unset or `false`, the API runs exactly as before and needs no broker. |
| `EMITTER_SECRET_KEY` | yes | The secret (master) key paired with the license. Used only to ask the broker for channel keys. Never leaves the API. |
| `EMITTER_HOST` | no | `emitter.railway.internal` |
| `EMITTER_PORT` | no | `8080` |
| `EMITTER_PUBLIC_URL` | no | The `wss://` URL handed to clients. |

## What the API does

Code lives in `features/realtime`. Everything goes through the `RealtimeGateway`
interface: `EmitterRealtimeGateway` when realtime is enabled, `DisabledRealtimeGateway`
otherwise.

### Channels

| Channel | Subscribers | Carries |
| --- | --- | --- |
| `makalu/store/{storeId}/orders/` | the store's web sessions | every order event of the store |
| `makalu/order/{orderId}/` | the customer who placed the order | the events of that order |

Only the API publishes, with a write key for `makalu/#/` that it requests after every
(re)connect and keeps in memory.

### Events

`OrderService` raises an `OrderChangedEvent` when it creates an order and on every status
change. `OrderRealtimeNotifier` publishes it after the transaction commits, to both
channels, at QoS 0. A rolled-back transaction publishes nothing.

```json
{
  "v": 1,
  "type": "order.created",
  "orderId": "42",
  "storeId": "7",
  "status": "PENDING",
  "updatedAt": "2026-11-13T18:04:11.123456Z"
}
```

`type` is `order.created` or `order.status_changed`. `updatedAt` is the value the REST
API returns as `updated_at` for the same change. The payload carries no personal data.

Publishing never blocks a request and never fails it. A failure is logged with the order
id and counted in `makalu.realtime.publish.failures`; `makalu.realtime.connected` is 1
while the API holds a broker connection. The client reconnects on its own with backoff
(1 s up to 30 s), including when the broker is down at startup.

### Credentials endpoints

| Endpoint | Who | Channel |
| --- | --- | --- |
| `GET /orders/{orderId}/realtime` | the customer who placed the order | `makalu/order/{orderId}/` |
| `GET /orders/stores/{storeId}/realtime` | the owner of the store | `makalu/store/{storeId}/orders/` |

```json
{
  "url": "wss://emitter-development.up.railway.app/",
  "channel": "makalu/order/42/",
  "key": "<read-only channel key>",
  "expires_at": "2026-11-14T18:04:11Z"
}
```

- The key is read-only, matches exactly that channel, and lives 24 hours, the lifetime
  of an access token (`JwtService.accessTokenValidityMs`).
- An order or store that is not the caller's answers `404`, like one that does not exist.
- `503` with `MK-801` means realtime is disabled or the broker did not answer. Clients
  carry on with REST alone.
- A client subscribes to `<key>/<channel>` and receives messages on `<channel>`.

## Facts about Emitter worth remembering

Checked against the source and a local `v3.1` broker.

- The broker speaks MQTT 3.1.1. It accepts an MQTT 5 connection and then refuses every
  subscribe, so every client must be pinned to 3.1.1.
- A topic is `<key>/<channel>/`, trailing slash required, options in a query string
  (`?ttl=…`). Messages arrive on `<channel>/`, without the key.
- Keys come from a keygen request: publish
  `{"key": "<secret key>", "channel": "…/", "type": "r", "ttl": <seconds>}` to
  `emitter/keygen/`; the answer comes back on the same connection, on the same topic.
- A key for `a/b/` matches exactly `a/b/` — not `a/b/c/`, not `a/+/`. Only a key
  generated for `a/#/` covers a subtree.
- A key's expiry is checked when subscribing. A subscription made before the expiry
  keeps receiving after it.
- A refused subscribe gets SUBACK `0x80` and a message on `emitter/error/`. A refused
  publish is only reported on `emitter/error/`.
- Without `EMITTER_LICENSE` the broker prints a freshly generated license and secret key
  and exits. It never runs without one.
- Anyone can open an MQTT connection; only channel keys gate access. The public port also
  serves `/health` and `/keygen` (a form that asks for the secret key). Neither can be
  turned off.

## Local development

```bash
cp .env.example .env
docker run --rm emitter/server:v3.1 ./emitter license new   # prints a license and a secret key
# put the license in EMITTER_LICENSE and the secret key in EMITTER_SECRET_KEY, in .env
docker compose up -d emitter
```

The broker is then on `localhost:8090` (TCP and `ws://localhost:8090/`). `.env` is
untracked; the pair in it is for your machine only.

Run the API with `REALTIME_ENABLED=true` and `EMITTER_SECRET_KEY` set to the secret key
from `.env`. Host, port and public URL default to this local broker.

## Tests

`./gradlew test` needs Docker: the tests start PostGIS and Emitter with Testcontainers.
`EmitterRealtimeGatewayIT` generates a throwaway license for its own broker.

## Rotating the license

Rotating replaces the license and the secret key together and invalidates every channel
key issued so far.

1. Generate a new pair: `docker run --rm emitter/server:v3.1 ./emitter license new`.
2. Set the `generated new license` value as `EMITTER_LICENSE` on `emitter`.
3. Set the `generated new secret key` value as `EMITTER_SECRET_KEY` on `api`.
4. Deploy `emitter`, then `api`.

The broker restart drops every connection. Clients reconnect, have their old key
refused, request fresh credentials from the API and refetch over REST, so nothing is
lost beyond a few seconds of live updates.

## Upgrading the broker

Change the image tag on the Railway service and the tag and digest in
`docker-compose.yml` together. The license does not change with the image.
