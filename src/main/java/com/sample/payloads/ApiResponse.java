package com.sample.payloads;

/**
 * Полезная нагрузка для json-ответа.
 * Класс сформирует json-объект, который возвратится в теле ответа.
 * Ответ типа "успешно-неуспешно" с информационным сообщением.
 */

public class ApiResponse {

    private boolean success;
    private String message;

    public ApiResponse(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean getSuccess(){
        return success;
    }
}
