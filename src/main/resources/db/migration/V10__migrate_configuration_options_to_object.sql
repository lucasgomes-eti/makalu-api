UPDATE menu_items
SET configurations = (SELECT jsonb_agg(
                                     jsonb_set(
                                             config,
                                             '{options}',
                                             (SELECT jsonb_agg(
                                                             jsonb_build_object(
                                                                     'name', option_value,
                                                                     'additionalPrice', 0
                                                             )
                                                     )
                                              FROM jsonb_array_elements_text(config -> 'options') AS option_value)
                                     )
                             )
                      FROM jsonb_array_elements(configurations) AS config)
WHERE configurations IS NOT NULL
    AND configurations != '[]'::jsonb
    AND (configurations -> 0 -> 'options' -> 0) IS NOT DISTINCT FROM NULL
   OR jsonb_typeof(configurations -> 0 -> 'options' -> 0) = 'string';