package eti.lucasgomes.makalu.shared.validations

import eti.lucasgomes.makalu.shared.GlobalError
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [PhoneNumberAvailableValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class PhoneNumberAvailable(
    val message: String = GlobalError.PHONE_NUMBER_TAKEN_MESSAGE,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)