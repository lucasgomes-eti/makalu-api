package eti.lucasgomes.makalu.shared.exceptions

import eti.lucasgomes.makalu.shared.MakaluError

class NotFoundException(val mkError: MakaluError) : Exception(mkError.message)