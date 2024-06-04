package Entity;

public class ApiResponse {
    protected String uploadResponseCode;
    protected String userId;
    protected int numberOfRecords;
    protected String nameOfRecords;
    protected String message;

    public ApiResponse() {
    }

    public ApiResponse(String uploadResponseCode, String userId, int numberOfRecords, String nameOfRecords, String message) {
        this.uploadResponseCode = uploadResponseCode;
        this.userId = userId;
        this.numberOfRecords = numberOfRecords;
        this.nameOfRecords = nameOfRecords;
        this.message = message;
    }

    public String getUploadResponseCode() {
        return uploadResponseCode;
    }

    public void setUploadResponseCode(String uploadResponseCode) {
        this.uploadResponseCode = uploadResponseCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public String getNameOfRecords() {
        return nameOfRecords;
    }

    public void setNameOfRecords(String nameOfRecords) {
        this.nameOfRecords = nameOfRecords;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
