package eti.lucasgomes.makalu.shared.exceptions

import eti.lucasgomes.makalu.shared.MakaluError

class BadRequestException(mkError: MakaluError, override val cause: Throwable? = null) : Exception(mkError.message)