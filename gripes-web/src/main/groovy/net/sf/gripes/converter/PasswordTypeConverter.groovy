package net.sf.gripes.converter


import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import net.sourceforge.stripes.util.Base64
import net.sourceforge.stripes.validation.ValidationError
import net.sourceforge.stripes.validation.TypeConverter


class PasswordTypeConverter implements TypeConverter<String> {
    String convert(String input, Class<? extends String> cls,Collection<ValidationError> errors) {
        hash(input)
    }
    String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1")
            byte[] bytes = md.digest(password.getBytes())

            return Base64.encodeBytes(bytes);
        } catch (NoSuchAlgorithmException exc) {
            throw new IllegalArgumentException(exc)
        }
    }
    public void setLocale(Locale locale) { }
}

