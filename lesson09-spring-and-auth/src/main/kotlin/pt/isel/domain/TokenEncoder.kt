package pt.isel.domain

/**
 * Defines the contract for encoding tokens into a validation representation.
 * Implementations are responsible for transforming a raw token into a
 * {@link TokenValidationInfo}, which can be used to verify token integrity
 * and contains the hashed token.
 */
interface TokenEncoder {
    /**
     * Creates validation information for the given token.
     * The exact encoding or hashing strategy depends on the implementation.
     *
     * @param token the raw token to be transformed
     * @return a {@link TokenValidationInfo} containing the validation representation of the token
     */
    fun createValidationInformation(token: String): TokenValidationInfo
}
