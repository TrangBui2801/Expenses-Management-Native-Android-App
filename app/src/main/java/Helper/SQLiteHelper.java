package Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import java.util.ArrayList;

import Entity.Expense;
import Entity.Trip;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "expense_management_db";

    public static final String TRIP_TABLE = "trips";
    public static final String EXPENSE_TABLE = "expenses";

    public static final String TRIP_ID_COLUMN = "ID";
    public static final String TRIP_TYPE_COLUMN = "type";
    public static final String TRIP_NAME_COLUMN = "name";
    public static final String TRIP_DESTINATION_COLUMN = "destination";
    public static final String TRIP_START_DATE_COLUMN = "startDate";
    public static final String TRIP_END_DATE_COLUMN = "endDate";
    public static final String TRIP_TRANSPORT_TYPE_COLUMN = "transportType";
    public static final String TRIP_TRANSPORT_NAME_COLUMN = "transportName";
    public static final String TRIP_ROUTE_COLUMN = "route";
    public static final String TRIP_RISK_ASSESSMENT_COLUMN = "riskAssessment";
    public static final String TRIP_DESCRIPTION_COLUMN = "description";

    public static final String EXPENSE_ID_COLUMN = "ID";
    public static final String EXPENSE_TRIP_ID_COLUMN = "tripID";
    public static final String EXPENSE_TYPE_COLUMN = "type";
    public static final String EXPENSE_AMOUNT_COLUMN = "amount";
    public static final String EXPENSE_DATE_COLUMN = "date";
    public static final String EXPENSE_TIME_COLUMN = "time";
    public static final String EXPENSE_COMMENT_COLUMN = "comment";
    public static final String EXPENSE_IMAGE_URL_COLUMN = "imageURL";
    public static final String EXPENSE_LOCATION_COLUMN = "location";

    public static final String CREATE_TRIP_TABLE = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s VARCHAR(100) NOT NULL, " +
                    " %s VARCHAR(255) NOT NULL, " +
                    " %s VARCHAR(50) NOT NULL, " +
                    " %s VARCHAR(50) NOT NULL, " +
                    " %s VARCHAR(100) NOT NULL, " +
                    " %s VARCHAR(255) NOT NULL, " +
                    " %s TEXT, " +
                    " %s VARCHAR(50) NOT NULL, " +
                    " %s TEXT, " +
                    " %s VARCHAR(100) NOT NULL)",
            TRIP_TABLE, TRIP_ID_COLUMN, TRIP_NAME_COLUMN, TRIP_DESTINATION_COLUMN, TRIP_START_DATE_COLUMN, TRIP_END_DATE_COLUMN, TRIP_TRANSPORT_TYPE_COLUMN, TRIP_TRANSPORT_NAME_COLUMN, TRIP_ROUTE_COLUMN, TRIP_RISK_ASSESSMENT_COLUMN, TRIP_DESCRIPTION_COLUMN, TRIP_TYPE_COLUMN
    );

    public static final String CREATE_EXPENSE_TABLE = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s INTEGER NOT NULL, " +
                    " %s VARCHAR(100) NOT NULL, " +
                    " %s INTEGER NOT NULL, " +
                    " %s VARCHAR(50) NOT NULL, " +
                    " %s VARCHAR(50) NOT NULL, " +
                    " %s TEXT, " +
                    " %s VARCHAR(255) NOT NULL, " +
                    " %s VARCHAR(255))",
            EXPENSE_TABLE, EXPENSE_ID_COLUMN, EXPENSE_TRIP_ID_COLUMN, EXPENSE_TYPE_COLUMN, EXPENSE_AMOUNT_COLUMN, EXPENSE_DATE_COLUMN, EXPENSE_TIME_COLUMN, EXPENSE_COMMENT_COLUMN, EXPENSE_IMAGE_URL_COLUMN, EXPENSE_LOCATION_COLUMN
    );

    private SQLiteDatabase database;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TRIP_TABLE);
        sqLiteDatabase.execSQL(CREATE_EXPENSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
        onCreate(database);
    }

    public long addTrip(Trip trip) {
        ContentValues data = new ContentValues();
        data.put(TRIP_NAME_COLUMN, trip.getName());
        data.put(TRIP_DESTINATION_COLUMN, trip.getDestination());
        data.put(TRIP_START_DATE_COLUMN, trip.getFromDate());
        data.put(TRIP_END_DATE_COLUMN, trip.getToDate());
        data.put(TRIP_TRANSPORT_TYPE_COLUMN, trip.getTransportType());
        data.put(TRIP_TRANSPORT_NAME_COLUMN, trip.getTransportName());
        data.put(TRIP_ROUTE_COLUMN, StringHelper.convertListRouteToJSONString(trip.getTravelRoutes()));
        data.put(TRIP_RISK_ASSESSMENT_COLUMN, trip.getRiskAssessment());
        data.put(TRIP_DESCRIPTION_COLUMN, trip.getDescription());
        data.put(TRIP_TYPE_COLUMN, trip.getType());

        long result = database.insertOrThrow(TRIP_TABLE, null, data);
        return result;
    }

    public boolean editTrip(Trip trip) {
        ContentValues data = new ContentValues();
        data.put(TRIP_NAME_COLUMN, trip.getName());
        data.put(TRIP_DESTINATION_COLUMN, trip.getDestination());
        data.put(TRIP_START_DATE_COLUMN, trip.getFromDate());
        data.put(TRIP_END_DATE_COLUMN, trip.getToDate());
        data.put(TRIP_TRANSPORT_TYPE_COLUMN, trip.getTransportType());
        data.put(TRIP_TRANSPORT_NAME_COLUMN, trip.getTransportName());
        data.put(TRIP_ROUTE_COLUMN, StringHelper.convertListRouteToJSONString(trip.getTravelRoutes()));
        data.put(TRIP_RISK_ASSESSMENT_COLUMN, trip.getRiskAssessment());
        data.put(TRIP_DESCRIPTION_COLUMN, trip.getDescription());
        data.put(TRIP_TYPE_COLUMN, trip.getType());
        long result = -1;
        Cursor cursor = database.rawQuery("SELECT * FROM " + TRIP_TABLE + " WHERE " + TRIP_ID_COLUMN + " = " + String.valueOf(trip.getID()), null);
        if (cursor.getCount() > 0) {
            result = database.update(TRIP_TABLE, data, TRIP_ID_COLUMN + " = " + String.valueOf(trip.getID()), null);
        }
        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean deleteTrip(int ID) {
        long result = -1;
        long deleteExpensesResult = -1;
        Cursor cursor = database.rawQuery("SELECT * FROM " + TRIP_TABLE + " WHERE " + TRIP_ID_COLUMN + " = " + String.valueOf(ID), null);
        if (cursor.getCount() > 0) {
            result = database.delete(TRIP_TABLE, TRIP_ID_COLUMN + " = " + String.valueOf(ID), null);
            deleteExpensesResult = database.delete(EXPENSE_TABLE, EXPENSE_TRIP_ID_COLUMN + " = " + String.valueOf(ID), null);
        }
        if (result == -1 || deleteExpensesResult == -1) {
            return false;
        }
        return true;
    }

    public boolean deleteTrips(List<Integer> tripIDs) {
        String args = "(";
        int index = 0;
        for (int ID : tripIDs) {
            args += String.valueOf(ID);
            index++;
            if (index != tripIDs.size()) {
                args += ", ";
            }
        }
        args += ")";
        long result = database.delete(TRIP_TABLE, TRIP_ID_COLUMN + " IN " + args, null);
        long deleteExpensesResult = database.delete(EXPENSE_TABLE, EXPENSE_TRIP_ID_COLUMN + " IN " + args, null);
        if (result == -1 || deleteExpensesResult == -1) {
            return false;
        }
        return true;
    }

    public List<Trip> getTrips(boolean isShowDeleteIcon, List<Integer> checkedIds) {
        Cursor cursor = database.rawQuery(
                "SELECT t.*, e.BASE_TOTAL FROM " + TRIP_TABLE + " t " +
                        "LEFT JOIN " +
                        "(SELECT SUM(" + EXPENSE_AMOUNT_COLUMN + ") AS BASE_TOTAL, " +
                        EXPENSE_TRIP_ID_COLUMN + " " +
                        "FROM " + EXPENSE_TABLE +
                        " GROUP BY " + EXPENSE_TRIP_ID_COLUMN + ") e " +
                        "ON t." + TRIP_ID_COLUMN + " = e." + EXPENSE_TRIP_ID_COLUMN + " " +
                        "ORDER BY t." + TRIP_ID_COLUMN + " DESC",
                null);
        List<Trip> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Trip trip = new Trip();
            int ID = cursor.getInt(0);
            trip.setID(ID);
            trip.setName(cursor.getString(1));
            trip.setDestination(cursor.getString(2));
            trip.setFromDate(cursor.getString(3));
            trip.setToDate(cursor.getString(4));
            trip.setTransportType(cursor.getString(5));
            trip.setTransportName(cursor.getString(6));
            String temp = cursor.getString(7);
            trip.setTravelRoutes(StringHelper.convertJSONStringToListRoute(temp));
            trip.setRiskAssessment(cursor.getString(8));
            trip.setDescription(cursor.getString(9));
            trip.setType(cursor.getString(10));
            trip.setTotalExpense(cursor.getLong(11));
            trip.setShowDeleteCheckbox(isShowDeleteIcon);
            trip.setDeleteCheckboxChecked(checkedIds.contains(ID));
            result.add(trip);
            cursor.moveToNext();
        }
        return result;
    }

    public Trip getTripByID(int ID) {
        Cursor cursor = database.rawQuery(
                "SELECT t.*, e.BASE_TOTAL FROM " + TRIP_TABLE + " t " +
                        "LEFT JOIN " +
                        "(SELECT SUM(" + EXPENSE_AMOUNT_COLUMN + ") AS BASE_TOTAL, " +
                        EXPENSE_TRIP_ID_COLUMN + " " +
                        "FROM " + EXPENSE_TABLE +
                        " GROUP BY " + EXPENSE_TRIP_ID_COLUMN + ") e " +
                        "ON t." + TRIP_ID_COLUMN + " = e." + EXPENSE_TRIP_ID_COLUMN + " " +
                        " WHERE t." + TRIP_ID_COLUMN + " = " + String.valueOf(ID),
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Trip trip = new Trip();
            trip.setID(cursor.getInt(0));
            trip.setName(cursor.getString(1));
            trip.setDestination(cursor.getString(2));
            trip.setFromDate(cursor.getString(3));
            trip.setToDate(cursor.getString(4));
            trip.setTransportType(cursor.getString(5));
            trip.setTransportName(cursor.getString(6));
            String temp = cursor.getString(7);
            trip.setTravelRoutes(StringHelper.convertJSONStringToListRoute(temp));
            trip.setRiskAssessment(cursor.getString(8));
            trip.setDescription(cursor.getString(9));
            trip.setType(cursor.getString(10));
            trip.setTotalExpense(cursor.getLong(11));
            return trip;
        }
        return new Trip();
    }

    public List<Trip> searchTrip(String searchContent, boolean isShowDeleteCheckBox, List<Integer> listCheckedItem) {
        String condition = searchContent.toLowerCase();
        Cursor cursor = database.rawQuery(
                "SELECT t.*, e.BASE_TOTAL FROM " + TRIP_TABLE + " t " +
                        "LEFT JOIN " +
                        "(SELECT SUM(" + EXPENSE_AMOUNT_COLUMN + ") AS BASE_TOTAL, " +
                        EXPENSE_TRIP_ID_COLUMN + " " +
                        "FROM " + EXPENSE_TABLE +
                        " GROUP BY " + EXPENSE_TRIP_ID_COLUMN + ") e " +
                        "ON t." + TRIP_ID_COLUMN + " = e." + EXPENSE_TRIP_ID_COLUMN + " WHERE "
                        + TRIP_NAME_COLUMN + " LIKE '%" + condition + "%' OR "
                        + TRIP_DESCRIPTION_COLUMN + " LIKE '%" + condition + "%' OR "
                        + TRIP_DESTINATION_COLUMN + " LIKE '%" + condition + "%' OR "
                        + TRIP_TRANSPORT_TYPE_COLUMN + " LIKE '%" + condition + "%' OR "
                        + TRIP_TRANSPORT_NAME_COLUMN + " LIKE '%" + condition + "%'"
                        + " ORDER BY " + TRIP_NAME_COLUMN + " DESC", null);
        List<Trip> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Trip trip = new Trip();
            int ID = cursor.getInt(0);
            trip.setID(ID);
            trip.setName(cursor.getString(1));
            trip.setDestination(cursor.getString(2));
            trip.setFromDate(cursor.getString(3));
            trip.setToDate(cursor.getString(4));
            trip.setTransportType(cursor.getString(5));
            trip.setTransportName(cursor.getString(6));
            String temp = cursor.getString(7);
            trip.setTravelRoutes(StringHelper.convertJSONStringToListRoute(temp));
            trip.setRiskAssessment(cursor.getString(8));
            trip.setDescription(cursor.getString(9));
            trip.setType(cursor.getString(10));
            trip.setTotalExpense(cursor.getLong(11));
            trip.setShowDeleteCheckbox(isShowDeleteCheckBox);
            trip.setDeleteCheckboxChecked(listCheckedItem.contains(ID));
            result.add(trip);
            cursor.moveToNext();
        }
        return result;
    }

    public long addExpense(Expense expense) {
        ContentValues data = new ContentValues();

        data.put(EXPENSE_TRIP_ID_COLUMN, expense.getTripID());
        data.put(EXPENSE_TYPE_COLUMN, expense.getType());
        data.put(EXPENSE_AMOUNT_COLUMN, expense.getAmount());
        data.put(EXPENSE_DATE_COLUMN, expense.getDate());
        data.put(EXPENSE_TIME_COLUMN, expense.getTime());
        data.put(EXPENSE_COMMENT_COLUMN, expense.getComment());
        data.put(EXPENSE_IMAGE_URL_COLUMN, expense.getImageUrl());
        data.put(EXPENSE_LOCATION_COLUMN, expense.getLocation());

        return database.insertOrThrow(EXPENSE_TABLE, null, data);
    }

    public boolean editExpense(Expense expense) {
        ContentValues data = new ContentValues();

        data.put(EXPENSE_TRIP_ID_COLUMN, expense.getTripID());
        data.put(EXPENSE_TYPE_COLUMN, expense.getType());
        data.put(EXPENSE_AMOUNT_COLUMN, expense.getAmount());
        data.put(EXPENSE_DATE_COLUMN, expense.getDate());
        data.put(EXPENSE_TIME_COLUMN, expense.getTime());
        data.put(EXPENSE_COMMENT_COLUMN, expense.getComment());
        data.put(EXPENSE_IMAGE_URL_COLUMN, expense.getImageUrl());
        data.put(EXPENSE_LOCATION_COLUMN, expense.getLocation());

        long result = -1;
        Cursor cursor = database.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE " + EXPENSE_ID_COLUMN + " = " + String.valueOf(expense.getID()), null);
        if (cursor.getCount() > 0) {
            result = database.update(EXPENSE_TABLE, data, EXPENSE_ID_COLUMN + " = " + String.valueOf(expense.getID()), null);
        }
        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean deleteExpense(int ID) {
        long result = -1;
        Cursor cursor = database.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE " + EXPENSE_ID_COLUMN + " = " + String.valueOf(ID), null);
        if (cursor.getCount() > 0) {
            result = database.delete(EXPENSE_TABLE, EXPENSE_ID_COLUMN + " = " + String.valueOf(ID), null);
        }
        if (result == -1) {
            return false;
        }
        return true;
    }

    public List<Expense> getExpensesByTrip(int ID, boolean isShowDeleteIcon, List<Integer> checkedIds) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE " + EXPENSE_TRIP_ID_COLUMN + " = " + String.valueOf(ID) + " ORDER BY " + EXPENSE_ID_COLUMN + " DESC", null);
        List<Expense> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = new Expense();
            int expenseID = cursor.getInt(0);
            expense.setID(expenseID);
            expense.setTripID(cursor.getInt(1));
            expense.setType(cursor.getString(2));
            expense.setAmount(cursor.getLong(3));
            expense.setDate(cursor.getString(4));
            expense.setTime(cursor.getString(5));
            expense.setComment(cursor.getString(6));
            expense.setShowDeleteCheckbox(isShowDeleteIcon);
            expense.setDeleteCheckboxChecked(checkedIds.contains(expenseID));
            expense.setImageUrl(cursor.getString(7));
            expense.setLocation(cursor.getString(8));
            result.add(expense);
            cursor.moveToNext();
        }
        return result;
    }

    public List<Expense> getExpensesByTrip(int ID, boolean isShowDeleteIcon, List<Integer> checkedIds, boolean isSortDesc) {
        String sortOrder = "";
        if (isSortDesc) {
            sortOrder = "DESC";
        } else {
            sortOrder = "ASC";
        }
        Cursor cursor = database.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE " + EXPENSE_TRIP_ID_COLUMN + " = " + String.valueOf(ID) + " ORDER BY " + EXPENSE_AMOUNT_COLUMN + " " + sortOrder, null);
        List<Expense> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = new Expense();
            int expenseID = cursor.getInt(0);
            expense.setID(expenseID);
            expense.setTripID(cursor.getInt(1));
            expense.setType(cursor.getString(2));
            expense.setAmount(cursor.getLong(3));
            expense.setDate(cursor.getString(4));
            expense.setTime(cursor.getString(5));
            expense.setComment(cursor.getString(6));
            expense.setShowDeleteCheckbox(isShowDeleteIcon);
            expense.setDeleteCheckboxChecked(checkedIds.contains(expenseID));
            expense.setImageUrl(cursor.getString(7));
            expense.setLocation(cursor.getString(8));
            result.add(expense);
            cursor.moveToNext();
        }
        return result;
    }

    public Expense getExpenseByID(int ID) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE " + EXPENSE_ID_COLUMN + " = " + String.valueOf(ID), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = new Expense();
            expense.setID(cursor.getInt(0));
            expense.setTripID(cursor.getInt(1));
            expense.setType(cursor.getString(2));
            expense.setAmount(cursor.getLong(3));
            expense.setDate(cursor.getString(4));
            expense.setTime(cursor.getString(5));
            expense.setComment(cursor.getString(6));
            expense.setImageUrl(cursor.getString(7));
            expense.setLocation(cursor.getString(8));
            return expense;
        }
        return new Expense();
    }

    public List<Expense> searchExpense(String searchContent, int tripID, boolean isShowDeleteIcon, List<Integer> checkedIds) {
        String condition = searchContent.toLowerCase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE "
                + EXPENSE_TRIP_ID_COLUMN + " = " + tripID + " AND ("
                + EXPENSE_TYPE_COLUMN + " LIKE '%" + condition + "%' OR "
                + EXPENSE_TIME_COLUMN + " LIKE '%" + condition + "%' OR "
                + EXPENSE_COMMENT_COLUMN + " LIKE '%" + condition + "%')"
                + " ORDER BY " + EXPENSE_ID_COLUMN + " DESC", null);
        List<Expense> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = new Expense();
            int ID = cursor.getInt(0);
            expense.setID(ID);
            expense.setTripID(cursor.getInt(1));
            expense.setType(cursor.getString(2));
            expense.setAmount(cursor.getLong(3));
            expense.setDate(cursor.getString(4));
            expense.setTime(cursor.getString(5));
            expense.setComment(cursor.getString(6));
            expense.setShowDeleteCheckbox(isShowDeleteIcon);
            expense.setDeleteCheckboxChecked(checkedIds.contains(ID));
            expense.setImageUrl(cursor.getString(7));
            expense.setLocation(cursor.getString(8));
            result.add(expense);
            cursor.moveToNext();
        }
        return result;
    }

    public List<Expense> searchExpense(String searchContent, int tripID, boolean isShowDeleteIcon, List<Integer> checkedIds, boolean isSortDesc) {
        String sortOrder = "";
        if (isSortDesc) {
            sortOrder = "DESC";
        } else {
            sortOrder = "ASC";
        }
        String condition = searchContent.toLowerCase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + EXPENSE_TABLE + " WHERE "
                + EXPENSE_TRIP_ID_COLUMN + " = " + tripID + " AND ("
                + EXPENSE_TYPE_COLUMN + " LIKE '%" + condition + "%' OR "
                + EXPENSE_TIME_COLUMN + " LIKE '%" + condition + "%' OR "
                + EXPENSE_COMMENT_COLUMN + " LIKE '%" + condition + "%')"
                + " ORDER BY " + EXPENSE_AMOUNT_COLUMN + " " + sortOrder, null);
        List<Expense> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = new Expense();
            int ID = cursor.getInt(0);
            expense.setID(ID);
            expense.setTripID(cursor.getInt(1));
            expense.setType(cursor.getString(2));
            expense.setAmount(cursor.getLong(3));
            expense.setDate(cursor.getString(4));
            expense.setTime(cursor.getString(5));
            expense.setComment(cursor.getString(6));
            expense.setShowDeleteCheckbox(isShowDeleteIcon);
            expense.setDeleteCheckboxChecked(checkedIds.contains(ID));
            expense.setImageUrl(cursor.getString(7));
            expense.setLocation(cursor.getString(8));
            result.add(expense);
            cursor.moveToNext();
        }
        return result;
    }

    public boolean deleteExpenses(List<Integer> expenseIDs) {
        String args = "(";
        int index = 0;
        for (int ID : expenseIDs) {
            args += String.valueOf(ID);
            index++;
            if (index != expenseIDs.size()) {
                args += ", ";
            }
        }
        args += ")";
        long rs = database.delete(EXPENSE_TABLE, EXPENSE_ID_COLUMN + " IN " + args, null);
        if (rs == -1) {
            return false;
        }
        return true;
    }
}
