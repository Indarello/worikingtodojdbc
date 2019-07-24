package com.sample.payloads;

/**
 * Полезная нагрузка для json-ответа.
 * Класс сформирует json-объект, который возвратится в теле ответа.
 * Объект возвратит данные для аутентификации: токен пользователя и тип токена.
 */
public class AuthenticationResponse
{

    private String accessToken;

    private String tokenType = "Bearer";

    public AuthenticationResponse(String token)
    {
        this.accessToken = token;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getTokenType()
    {
        return tokenType;
    }

    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
}
