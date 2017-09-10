package seclass.gatech.edu.glm.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBUtility extends SQLiteOpenHelper {

    /**
     * DB Name and Version
     */
    private static final String DATABASE_NAME = "glm.db";
    private static final int DATABASE_VERSION = 1;

    /*************** Tables ***************************/
    /**
     * Table Names
     */
    private static final String TABLE_LISTS = "lists";
    private static final String TABLE_LISTITEMS = "list_items";
    private static final String TABLE_ITEMS = "items";

    /*************** Columns ***************************/
    /**
     * Common Columns
     */
    private static final String LIST_ID = "list_id";
    private static final String ITEM_ID = "item_id";

    /**
     * Table_Lists: A list == (list_id, list_name)
     */
    public static final String LIST_NAME = "list_name";

    /**
     * Table_ListItems: A listItem == (listItem_id, list_id, item_id, item_quantity, item_checked)
     */
    public static final String LISTITEM_ID = "listItem_id";
    public static final String ITEM_QUANTITY = "item_quantity";
    public static final String ITEM_CHECKED = "item_checked";

    /**
     * Table_Items: An item == (item_id, item_name, item_type)
     */
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_TYPE = "item_type";


    /***************
     * Table Creation
     ***************************/
    private static final String CREATE_TABLE_LISTS = "CREATE TABLE "
            + TABLE_LISTS + "("
            + LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LIST_NAME + " TEXT"
            + ")";

    private static final String CREATE_TABLE_LISTITEMS = "CREATE TABLE "
            + TABLE_LISTITEMS + "("
            + LISTITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LIST_ID + " INTEGER, "
            + ITEM_ID + " INTEGER, "
            + ITEM_QUANTITY + " REAL, "
            + ITEM_CHECKED + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE "
            + TABLE_ITEMS + "("
            + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ITEM_NAME + " TEXT, "
            + ITEM_TYPE + " TEXT"
            + ")";

    private static DBUtility m_instance;

    public static synchronized DBUtility getInstance(Context context) {
        if (m_instance == null) {
            Log.d("DBUtility", "Creating DBUtility instance");
            m_instance = new DBUtility(context.getApplicationContext());
        }
        return m_instance;
    }

    private DBUtility(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /** Create tables */
        Log.d("DBUtility", "Creating Tables");
        db.execSQL(CREATE_TABLE_LISTS);
        db.execSQL(CREATE_TABLE_LISTITEMS);
        db.execSQL(CREATE_TABLE_ITEMS);


        addItem(db, "Cherry", "Fruits");
        addItem(db, "Dragon Fruit", "Fruits");
        addItem(db, "Kiwi", "Fruits");
        addItem(db, "Guava", "Fruits");
        addItem(db, "Banana", "Fruits");
        addItem(db, "Dates", "Fruits");
        addItem(db, "Apple", "Fruits");
        addItem(db, "Pears", "Fruits");
        addItem(db, "Grapes", "Fruits");
        addItem(db, "Watermelon", "Fruits");
        addItem(db, "Cantaloupe", "Fruits");

        addItem(db, "Peanut", "Nuts");
        addItem(db, "Cashew", "Nuts");
        addItem(db, "Almond", "Nuts");
        addItem(db, "Brazil Nut", "Nuts");
        addItem(db, "Walnut", "Nuts");
        addItem(db, "Pecan", "Nuts");
        addItem(db, "Pistachios", "Nuts");
        addItem(db, "Macadamia", "Nuts");
        addItem(db, "Hazelnut", "Nuts");

        addItem(db, "Lamb", "Meat");
        addItem(db, "Beef", "Meat");
        addItem(db, "Pork", "Meat");
        addItem(db, "Chicken", "Meat");
        addItem(db, "Fish", "Meat");
        addItem(db, "Turkey", "Meat");
        addItem(db, "Ham", "Meat");
        addItem(db, "Shrimp", "Meat");
        addItem(db,"Drumsticks","Meat");
        addItem(db,"Tilapia","Meat");
        addItem(db,"Camel","Meat");
        addItem(db,"Buffalo","Meat");

        addItem(db, "Ground coffee", "Beverage");
        addItem(db, "Roast coffee", "Beverage");
        addItem(db, "Green tea", "Beverage");
        addItem(db, "Black tea", "Beverage");
        addItem(db, "Iced tea", "Beverage");
        addItem(db, "Lemon juice", "Beverage");
        addItem(db, "Apple juice", "Beverage");
        addItem(db, "Soda", "Beverage");
        addItem(db, "Pepsi", "Beverage");
        addItem(db, "Water", "Beverage");

        addItem(db, "Toilet Paper", "Toiletries");
        addItem(db, "Shampoo", "Toiletries");
        addItem(db, "Soap", "Toiletries");
        addItem(db, "Toothpaste", "Toiletries");
        addItem(db, "Brush", "Toiletries");
        addItem(db, "Razor", "Toiletries");
        addItem(db, "Nail Clipper", "Toiletries");
        addItem(db, "Perfume", "Toiletries");
        addItem(db, "Body Wash", "Toiletries");
        addItem(db, "Towel", "Toiletries");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /** On upgrade drop existing tables. */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        /** create new tables. */
        onCreate(db);
    }

    /***************
     * Lists Table Interactions
     ***************************/
    public GroceryList addList(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIST_NAME, listName);
        long listId = db.insert(TABLE_LISTS, null, values);
        return new GroceryList(listId, listName);
    }

    public void deleteList(long listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, LIST_ID + "=" + listId, null);
        db.delete(TABLE_LISTITEMS, LIST_ID + "=" + listId, null);
    }

    // delete all groceryLists
    public void deleteAllLists() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LISTS, null, null);
    }

    public GroceryList getList(long listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTS + " WHERE " + LIST_ID + "= ?", new String[]{"" + listId});
        if (cursor.moveToFirst()) {
            return new GroceryList(cursor.getInt(cursor.getColumnIndex(LIST_ID)), cursor.getString(cursor.getColumnIndex(LIST_NAME)));
        }
        return null;
    }

    // get/search list by list name
    public GroceryList getListbyName(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTS + " WHERE " + LIST_NAME + "= ?", new String[]{"" + listName});

        if (!cursor.moveToFirst()) {
            cursor.moveToFirst();
        }

        GroceryList groceryList = new GroceryList();
        groceryList.setId(cursor.getInt(cursor.getColumnIndex(LIST_ID)));
        groceryList.setName(cursor.getString(cursor.getColumnIndex(LIST_NAME)));

        return groceryList;
    }

    /**
     * get/search list ID by list name
     **/
    public int getListID(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTS + " WHERE " + LIST_NAME + "= ?", new String[]{"" + listName});

        if (!cursor.moveToFirst()) {
            cursor.moveToFirst();
        }
        int listId = cursor.getInt(cursor.getColumnIndex(LIST_ID));
        return listId;
    }

    /**
     * Return the list of the user's groceryLists
     */
    public List<GroceryList> getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTS, null);
        List<GroceryList> groceryLists = new ArrayList<GroceryList>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                GroceryList groceryList = new GroceryList(cursor.getInt(cursor.getColumnIndex(LIST_ID)), cursor.getString(cursor.getColumnIndex(LIST_NAME)));
                groceryLists.add(groceryList);
            } while (cursor.moveToNext());
        }
        return groceryLists;
    }

    public void renameList(long listId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIST_NAME, newName);
        db.update(TABLE_LISTS, values, LIST_ID + "=" + listId, null);
    }

    /***************
     * List_Item Table Interactions
     ***************************/
    public ListItem addListItem(long groceryListId, long itemId, int quantity, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIST_ID, groceryListId);
        values.put(ITEM_ID, itemId);
        values.put(ITEM_QUANTITY, quantity);
        values.put(ITEM_CHECKED, isChecked ? 1 : 0);
        long listItemId = db.insert(TABLE_LISTITEMS, null, values);
        return getListItem(listItemId);
    }

    public ListItem getListItem(long listItemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTITEMS + " WHERE " + LISTITEM_ID + "= ?", new String[]{"" + listItemId});
        if (cursor.moveToFirst()) {
            return new ListItem(
                    cursor.getInt(cursor.getColumnIndex(LISTITEM_ID)),
                    getItem(cursor.getInt(cursor.getColumnIndex(ITEM_ID))),
                    cursor.getFloat(cursor.getColumnIndex(ITEM_QUANTITY)),
                    cursor.getInt(cursor.getColumnIndex(ITEM_CHECKED)) == 1
            );
        }
        return null;
    }

    /**
     * Returns ItemTypes present in this groceryList, used as keys for listItems
     * ItemType strings are ordered alphabetically.
     */
    public List<String> getItemTypes(long groceryListId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> itemTypes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTITEMS + " WHERE " + LIST_ID + " = ?", new String[]{"" + groceryListId});
        if (cursor.moveToFirst()) {
            do {
                String itemType = getItem(cursor.getInt(cursor.getColumnIndex(ITEM_ID))).getType();
                if (!itemTypes.contains(itemType)) {
                    itemTypes.add(itemType);
                }
            } while (cursor.moveToNext());
        }
        Collections.sort(itemTypes);
        return itemTypes;
    }

    /**
     * Returns all listItems in this groceryList, grouped by itemType
     * ListItem lists are ordered by item name
     */
    public HashMap<String, List<ListItem>> getListItems(long groceryListId) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, List<ListItem>> hierarchicalListItems = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTITEMS + " WHERE " + LIST_ID + " = ?", new String[]{"" + groceryListId});
        if (cursor.moveToFirst()) {
            do {
                ListItem listItem = new ListItem(
                        cursor.getInt(cursor.getColumnIndex(LISTITEM_ID)),
                        getItem(cursor.getInt(cursor.getColumnIndex(ITEM_ID))),
                        cursor.getFloat(cursor.getColumnIndex(ITEM_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(ITEM_CHECKED)) == 1
                );
                if (!hierarchicalListItems.containsKey(listItem.getType())) {
                    hierarchicalListItems.put(listItem.getType(), new ArrayList<ListItem>());
                }
                hierarchicalListItems.get(listItem.getType()).add(listItem);
            } while (cursor.moveToNext());
        }
        for (String key : hierarchicalListItems.keySet()) {
            Collections.sort(hierarchicalListItems.get(key), new Comparator<ListItem>() {
                @Override
                public int compare(ListItem item1, ListItem item2) {
                    return fixCase(item1.getName()).compareTo(fixCase(item2.getName()));
                }
            });
        }
        return hierarchicalListItems;
    }

    /** Returns a set of all Items inside given list: Returning Items is consistent with the logic
     * of the app, and is easier for implementation purposes. */
    public Set<Item> getListItemsSet(long groceryListId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Set<Item> listItemsSet = new HashSet<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LISTITEMS + " WHERE " + LIST_ID + " = ?", new String[]{"" + groceryListId});
        if (cursor.moveToFirst()) {
            do {
                ListItem listItem = new ListItem(
                        cursor.getInt(cursor.getColumnIndex(LISTITEM_ID)),
                        getItem(cursor.getInt(cursor.getColumnIndex(ITEM_ID))),
                        cursor.getFloat(cursor.getColumnIndex(ITEM_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(ITEM_CHECKED)) == 1
                );
                listItemsSet.add(listItem);
            } while (cursor.moveToNext());
        }
        return listItemsSet;
    }

    public void setItemChecked(long listItemId, boolean checked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_CHECKED, checked ? 1 : 0);
        db.update(TABLE_LISTITEMS, values, LISTITEM_ID + "=" + listItemId, null);
    }

    // add list_item by list name and item name, maybe will be easier in implementation
    public long addListItembyName(String listName, String itemName, int quantity, String ischecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIST_ID, getListbyName(listName).getListId());
        values.put(ITEM_ID, getItembyName(itemName).getItemId());
        values.put(ITEM_QUANTITY, quantity);
        values.put(ITEM_CHECKED, ischecked);
        long listitem_id = db.insert(TABLE_LISTITEMS, null, values);
        return listitem_id;
    }

    /** get all items information including quantity and isChecked in a list by list id **/
    /*public List<ListItem> getOrderedListItemsByListId(int list_Id){

        List list_items = new ArrayList<ListItem>();

        // just keep it here
//        String selectQuery = "SELECT " + "table_items."
//                +ITEM_ID + ",table_lists."
//                +LIST_ID + ",table_listitems."
//                +LISTITEM_ID + ",table_items."
//                +ITEM_NAME+",table_items."
//                +ITEM_TYPE+",table_listitems."
//                +ITEM_CHECKED+",table_listitems."
//                +ITEM_QUANTITY
//                + " FROM "
//                + TABLE_ITEMS + " table_items, "
//                + TABLE_LISTS + " table_lists, "
//                + TABLE_LISTITEMS + " table_listitems WHERE table_lists."
//                + LIST_ID + " = '" + listId + "'"
//                + " AND table_lists." + LIST_ID + " = " + "table_listitems."+ LIST_ID
//                + " AND table_items." + ITEM_ID + " = " + "table_listitems."+ ITEM_ID
//                + " ORDER BY table_items." + ITEM_TYPE +", table_items." + ITEM_NAME + " ASC";

        String selectQuery = "SELECT * FROM "
                + TABLE_ITEMS + " table_items, "
                + TABLE_LISTS + " table_lists, "
                + TABLE_LISTITEMS + " table_listitems WHERE table_lists."
                + LIST_ID + " = '" + list_Id + "'"
                + " AND table_lists." + LIST_ID + " = " + "table_listitems."+ LIST_ID
                + " AND table_items." + ITEM_ID + " = " + "table_listitems."+ ITEM_ID
                + " ORDER BY table_items." + ITEM_TYPE +", table_items." + ITEM_NAME + " ASC";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do{
                ListItem listitem = new ListItem(
                        cursor.getInt(cursor.getColumnIndex(LISTITEM_ID)),
                        cursor.getInt(cursor.getColumnIndex(LIST_ID)),
                        cursor.getInt(cursor.getColumnIndex(ITEM_ID)),
                        cursor.getFloat(cursor.getColumnIndex(ITEM_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(ITEM_CHECKED))==1);

                list_items.add(listitem);
            }while(cursor.moveToNext());
        }

        return list_items;
    }*/

    /** get all items information including quantity and isChecked in a list by list name **/
    /*public List<ListItem> getOrderedListItemsByListName(String listName){

        List list_items = new ArrayList<ListItem>();

                String selectQuery = "SELECT * FROM "
                + TABLE_ITEMS + " table_items, "
                + TABLE_LISTS + " table_lists, "
                + TABLE_LISTITEMS + " table_listitems WHERE table_lists."
                + LIST_NAME + " = '" + listName + "'"
                + " AND table_lists." + LIST_ID + " = " + "table_listitems."+ LIST_ID
                + " AND table_items." + ITEM_ID + " = " + "table_listitems."+ ITEM_ID
                        + " ORDER BY table_items." + ITEM_TYPE +", table_items." + ITEM_NAME + " ASC";



        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do{
                ListItem listitem = new ListItem(
                        cursor.getInt(cursor.getColumnIndex(LISTITEM_ID)),
                        cursor.getInt(cursor.getColumnIndex(LIST_ID)),
                        cursor.getInt(cursor.getColumnIndex(ITEM_ID)),
                        cursor.getFloat(cursor.getColumnIndex(ITEM_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(ITEM_CHECKED))==1);


                list_items.add(listitem);
            }while(cursor.moveToNext());
        }

        return  list_items;
    }*/

    /**
     * get all items and just items without quantity or isChecked  in a list by list id
     **/
    public List<Item> getListItemsByListId(int listId) {

        List list_items = new ArrayList<Item>();

        String selectQuery = "SELECT * FROM "
                + TABLE_ITEMS + " table_items, "
                + TABLE_LISTS + " table_lists, "
                + TABLE_LISTITEMS + " table_listitems WHERE table_lists."
                + LIST_ID + " = '" + listId + "'"
                + " AND table_lists." + LIST_ID + " = " + "table_listitems." + LIST_ID
                + " AND table_items." + ITEM_ID + " = " + "table_listitems." + ITEM_ID;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(ITEM_ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(ITEM_NAME)));

                list_items.add(item);
            } while (cursor.moveToNext());
        }

        return list_items;
    }

    /**
     * get all items and just items and no quantity or isChecked in a list by list name
     **/
    public List<Item> getListItemsByListName(String listName) {

        List listItems = new ArrayList<Item>();

        String selectQuery = "SELECT * FROM "
                + TABLE_ITEMS + " table_items, "
                + TABLE_LISTS + " table_lists, "
                + TABLE_LISTITEMS + " table_listitems WHERE table_lists."
                + LIST_NAME + " = '" + listName + "'"
                + " AND table_lists." + LIST_ID + " = " + "table_listitems." + LIST_ID
                + " AND table_items." + ITEM_ID + " = " + "table_listitems." + ITEM_ID;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(ITEM_ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(ITEM_NAME)));

                listItems.add(item);
            } while (cursor.moveToNext());
        }

        return listItems;
    }

    public int changeQuantity(long listitemId, float quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_QUANTITY, quantity);
        return db.update(TABLE_LISTITEMS, values, LISTITEM_ID + " = ?", new String[]{String.valueOf(listitemId)});
    }

    //public void deleteListItem(long listItemId) {
    /** increase the quantity by 1 **/
    /*public int incrementQuantity(long listitem_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LISTITEMS + " WHERE "  + LISTITEM_ID + " = " + listitem_id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ContentValues values = new ContentValues();
        int oldQuantity = cursor.getInt(cursor.getColumnIndex(ITEM_QUANTITY));
        int newQuantity = oldQuantity + 1;
        values.put(ITEM_QUANTITY, newQuantity);
        return db.update(TABLE_LISTITEMS, values, LISTITEM_ID + " = ?", new String[]{String.valueOf(listitem_id)});
    }*/

    /**
     * decrease the quantity by 1
     **/
    public int decrementQuantity(long listitem_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LISTITEMS + " WHERE " + LISTITEM_ID + " = " + listitem_id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ContentValues values = new ContentValues();
        int oldQuantity = cursor.getInt(cursor.getColumnIndex(ITEM_QUANTITY));
        int newQuantity = oldQuantity - 1;
        values.put(ITEM_QUANTITY, newQuantity);
        return db.update(TABLE_LISTITEMS, values, LISTITEM_ID + " = ?", new String[]{String.valueOf(listitem_id)});
    }

    /**
     * check one item in the list
     **/
    public int checkListItem(long listitem_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_CHECKED, 1);
        return db.update(TABLE_LISTITEMS, values, LISTITEM_ID + " = ?", new String[]{String.valueOf(listitem_id)});
    }

    /**
     * uncheck one item in a list
     **/
    public int uncheckListItem(long listitem_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_CHECKED, 0);
        return db.update(TABLE_LISTITEMS, values, LISTITEM_ID + " = ?", new String[]{String.valueOf(listitem_id)});
    }

    /**
     * check all items in the list
     **/
    public void checkAllItem(long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_CHECKED, 1);
        db.update(TABLE_LISTITEMS, values, LIST_ID + " = ?", new String[]{String.valueOf(list_id)});
    }

    /**
     * uncheck all items in the list
     **/
    public void uncheckAllItem(long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_CHECKED, 0);
        db.update(TABLE_LISTITEMS, values, LIST_ID + " = ?", new String[]{String.valueOf(list_id)});
    }


    public void deleteListItem(long listItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTITEMS, LISTITEM_ID + " = ?", new String[]{String.valueOf(listItemId)});
    }

    // delete all listitems
    public void deleteAllListItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTITEMS, null, null);
    }


    /**
     * Item Table Interactions
     */
    public Item addItem(String itemName, String itemType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, itemName);
        values.put(ITEM_TYPE, itemType);
        long itemId = db.insert(TABLE_ITEMS, null, values);
        return new Item(itemId, itemName, itemType);
    }

    /**
     * Add Item, given a database
     */
    public Item addItem(SQLiteDatabase db, String itemName, String itemType) {
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, fixCase(itemName));
        values.put(ITEM_TYPE, fixCase(itemType));
        long itemId = db.insert(TABLE_ITEMS, null, values);
        return new Item(itemId, itemName, itemType);
    }


    public Item getItem(long itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + ITEM_ID + " = " + itemId;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return new Item(
                    cursor.getInt(cursor.getColumnIndex(ITEM_ID)),
                    cursor.getString(cursor.getColumnIndex(ITEM_NAME)),
                    cursor.getString(cursor.getColumnIndex(ITEM_TYPE)));
        }
        return null;
    }

    /**
     * Return list of all itemTypes
     **/
    public List<String> getAllItemTypes() {
        Set<String> allTypesSet = new HashSet<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                allTypesSet.add(cursor.getString(cursor.getColumnIndex(ITEM_TYPE)));
            } while (cursor.moveToNext());
        }
        List<String> allTypesList = new ArrayList<>();
        allTypesList.addAll(allTypesSet);
        Collections.sort(allTypesList);
        return allTypesList;
    }

    // get/search item by item name
    public Item getItembyName(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + ITEM_NAME + "= ?", new String[]{"" + itemName});

        if (!cursor.moveToFirst()) {
            cursor.moveToFirst();
        }

        Item item = new Item();
        item.setId(cursor.getInt(cursor.getColumnIndex(ITEM_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(ITEM_NAME)));
        item.setType(cursor.getString(cursor.getColumnIndex(ITEM_TYPE)));
        return item;
    }

    /**
     * get/search item by item name
     */
    public List<Item> getItemsByPartialNameAndType(String itemName, String itemType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (itemType.equals("")) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + ITEM_NAME + " LIKE ?", new String[]{"%" + itemName + "%"});
        } else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + ITEM_NAME + " LIKE ? AND " + ITEM_TYPE + " = ?", new String[]{"%" + itemName + "%", itemType});
        }
        List<Item> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(cursor.getColumnIndex(ITEM_ID)),
                        cursor.getString(cursor.getColumnIndex(ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndex(ITEM_TYPE))
                );

                items.add(item);
            } while (cursor.moveToNext());
        }
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return fixCase(item1.getName()).compareTo(fixCase(item2.getName()));
            }
        });
        return items;
    }

    /**
     * get/search item by item name
     */
    public List<Item> getItemsByPartialName(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + ITEM_NAME + " LIKE ?", new String[]{"%" + itemName + "%"});
        List<Item> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(cursor.getColumnIndex(ITEM_ID)),
                        cursor.getString(cursor.getColumnIndex(ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndex(ITEM_TYPE))
                );

                items.add(item);
            } while (cursor.moveToNext());
        }
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return fixCase(item1.getName()).compareTo(fixCase(item2.getName()));
            }
        });
        return items;
    }

    /**
     * get/search item by item name
     */
    public List<Item> getItemsByType(String itemType) {
        List items = new ArrayList<Item>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + ITEM_TYPE + "= ?", new String[]{"" + itemType});

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(ITEM_ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(ITEM_NAME)));
                item.setType(cursor.getString(cursor.getColumnIndex(ITEM_TYPE)));
                items.add(item);
            } while (cursor.moveToNext());
        }
        return items;
    }

    // delete all items, need be careful
    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ITEMS, null, null);
    }

    /**
     * Close database
     */
    public void closeDb() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private String fixCase(String s) {
        if (s.length()==0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

}
