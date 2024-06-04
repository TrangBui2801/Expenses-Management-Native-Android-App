package Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Trip {
    protected int ID;
    protected String name;
    protected String type;
    protected String destination;
    protected String fromDate;
    protected String toDate;
    protected String transportType;
    protected String transportName;
    protected List<TravelRoute> travelRoutes;
    protected String riskAssessment;
    protected String description;
    protected long totalExpense;
    protected boolean isShowDeleteCheckbox;
    protected boolean isDeleteCheckboxChecked;

    public Trip() {
        this.totalExpense = 0;
        this.isDeleteCheckboxChecked = false;
        this.isShowDeleteCheckbox = false;
    }

    public Trip(int ID, String type, String name, String destination, String fromDate, String toDate,String transportType, String transportName, List<TravelRoute> travelRoutes, String riskAssessment, String description) {
        this.ID = ID;
        this.type = type;
        this.name = name;
        this.destination = destination;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.transportType = transportType;
        this.transportName = transportName;
        this.travelRoutes = travelRoutes;
        this.riskAssessment = riskAssessment;
        this.description = description;
        this.totalExpense = 0;
        this.isDeleteCheckboxChecked = false;
        this.isShowDeleteCheckbox = false;
    }

    public Trip(String type, String name, String destination, String fromDate, String toDate, String transportType, String transportName, List<TravelRoute> travelRoutes, String riskAssessment, String description) {
        this.type = type;
        this.name = name;
        this.destination = destination;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.transportType = transportType;
        this.transportName = transportName;
        this.travelRoutes = travelRoutes;
        this.riskAssessment = riskAssessment;
        this.description = description;
        this.totalExpense = 0;
        this.isDeleteCheckboxChecked = false;
        this.isShowDeleteCheckbox = false;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public List<TravelRoute> getTravelRoutes() {
        return travelRoutes;
    }

    public void setTravelRoutes(List<TravelRoute> travelRoutes) {
        this.travelRoutes = travelRoutes;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(long totalExpense) {
        this.totalExpense = totalExpense;
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
}
