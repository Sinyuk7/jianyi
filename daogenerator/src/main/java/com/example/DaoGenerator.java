package com.example;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class DaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.sinyuk.jianyimaterial.model");
        schema.setDefaultJavaPackageDao("com.sinyuk.jianyimaterial.greendao.dao");
        schema.enableKeepSectionsByDefault();
        //schema.enableActiveEntitiesByDefault();
        //ActiveRecord
        addEntity(schema);

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    private static void addEntity(Schema schema) {

        Entity user = schema.addEntity("User");
        user.addStringProperty("id").primaryKey().unique();

        user.addStringProperty("status");
        user.addStringProperty("name");
        user.addStringProperty("lastlogin");
        user.addStringProperty("lastip");
        user.addStringProperty("email");
        user.addStringProperty("openid");
        user.addStringProperty("sex");
        user.addStringProperty("role_id");
        user.addStringProperty("realname");
        user.addStringProperty("province");
        user.addStringProperty("city");
        user.addStringProperty("country");
        user.addStringProperty("heading");
        user.addStringProperty("language");
        user.addStringProperty("Gamount");
        user.addStringProperty("tel");
        user.addStringProperty("self_words");
        user.addStringProperty("edu_id");
        user.addStringProperty("idcard");
        user.addStringProperty("self_introduction");
        user.addStringProperty("school");
        user.addStringProperty("last_x");
        user.addStringProperty("last_y");
        user.addStringProperty("current_school");

        user.implementsSerializable();


        // yihuoDetails
//        private List<Pics> pics;

        Entity yihuoDetails = schema.addEntity("YihuoDetails");
        Property addedDate = yihuoDetails.addDateProperty("date").getProperty();//  added date
        yihuoDetails.addStringProperty("id").notNull().primaryKey().unique(); // PK
        yihuoDetails.addStringProperty("name");
        yihuoDetails.addStringProperty("detail");
        yihuoDetails.addStringProperty("title");
        yihuoDetails.addStringProperty("price");
        yihuoDetails.addStringProperty("tel");
        yihuoDetails.addStringProperty("sort");
        yihuoDetails.addStringProperty("del");
        yihuoDetails.addStringProperty("top");
        yihuoDetails.addStringProperty("time");
        Property uIdYihuo = yihuoDetails.addStringProperty("uid").notNull().getProperty();//FK
        yihuoDetails.addStringProperty("way");
        yihuoDetails.addStringProperty("reason");
        yihuoDetails.addStringProperty("viewcount");
        yihuoDetails.addStringProperty("x");
        yihuoDetails.addStringProperty("y");
        yihuoDetails.addStringProperty("oldprice");
        yihuoDetails.addStringProperty("pic"); // cover url
        // TODO: add pics in keep section

        yihuoDetails.implementsSerializable();



        yihuoDetails.addToOne(user, uIdYihuo);

        ToMany userToYihuo = user.addToMany(yihuoDetails, uIdYihuo);
        userToYihuo.setName("yihuoLikes");
        userToYihuo.orderDesc(addedDate);

        Entity school = schema.addEntity("School");
        school.addStringProperty("id").notNull().primaryKey().unique();
        school.addStringProperty("name").notNull();
        school.addStringProperty("coord");
        school.addStringProperty("x");
        school.addStringProperty("y");

        school.implementsSerializable();
    }
}
