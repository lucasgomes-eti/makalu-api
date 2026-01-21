package eti.lucasgomes.makalu.shared.exceptions

import eti.lucasgomes.makalu.shared.MakaluError

class AuthErrorException(val mkError: MakaluError) : Exception(mkError.message)