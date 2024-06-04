package Entity;
public class Expense {
    protected int ID;
    protected int tripID;
    protected String type;
    protected long amount;
    protected String date;
    protected String time;
    protected String comment;
    protected boolean isShowDeleteCheckbox;
    protected boolean isDeleteCheckboxChecked;
    protected String imageUrl;
    protected String location;

    public Expense() {
        this.isDeleteCheckboxChecked = false;
        this.isShowDeleteCheckbox = false;
    }

    public Expense(int ID, int tripID, String type, long amount, String date, String time, String comment, String imageUrl, String location) {
        this.ID = ID;
        this.tripID = tripID;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.comment = comment;
        this.isDeleteCheckboxChecked = false;
        this.isShowDeleteCheckbox = false;
        this.imageUrl = imageUrl;
        this.location = location;
    }

    public Expense(int tripID, String type, long amount, String date, String time, String comment, String imageUrl, String location) {
        this.tripID = tripID;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.comment = comment;
        this.isDeleteCheckboxChecked = false;
        this.isShowDeleteCheckbox = false;
        this.imageUrl = imageUrl;
        this.location = location;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isShowDeleteCheckbox() {
        return isShowDeleteCheckbox;
    }

    public void setShowDeleteCheckbox(boolean showDeleteCheckbox) {
        isShowDeleteCheckbox = showDeleteCheckbox;
    }

    public boolean isDeleteCheckboxChecked() {
        return isDeleteCheckboxChecked;
    }

    public void setDeleteCheckboxChecked(boolean deleteCheckboxChecked) {
        isDeleteCheckboxChecked = deleteCheckboxChecked;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
