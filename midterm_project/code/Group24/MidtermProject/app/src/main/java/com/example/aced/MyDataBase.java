package com.example.aced;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyDataBase extends SQLiteOpenHelper {

    private static final String DB_NAME= "MyDataBase.db";
    private static final String HEROS_TABLE = "hero";
    private static final String SKILLS_TABLE = "skill";
    private static final String EQUIPMENT_TABLE = "equip";
    private static final String INSCRIPTION_TABLE ="inscription";
    private static final String HERO_INSCRIPTION_TABLE = "hero_inscription";
    private static final String HERO_EQUIP = "hero_equip";
    private static final String IMAGE_TABLE = "imagetable";
    private static final int DB_VERSION = 1;

    private static final Integer HERO_IMAGE_TYPE =0;
    private static final Integer SKILLS_IMAGE_TYPE =1;
    private static final Integer HEROBG_IMAGE_TYPE =2;
    private static final Integer EQUIPMENT_IMAGE_TYPE =3;
    private static final Integer INSCRIPTION_IMAGE_TYPE =4;

    public MyDataBase( Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE hero (\n" +
                        "  `hero_id` int(11) NOT NULL PRIMARY KEY,\n" +
                        "  `name` TEXT DEFAULT NULL,\n" +
                        "  `pay_type` int(11) DEFAULT NULL,\n" +
                        "  `new_type` int(11) DEFAULT NULL,\n" +
                        "  `hero_type` int(11) DEFAULT NULL,\n" +
                        "  `hero_type2` int(11) DEFAULT NULL,\n" +
                        "  `skin_name` TEXT DEFAULT NULL,\n" +
                        "  `img_url` TEXT DEFAULT NULL,\n" +
                        "  `live` int(11) DEFAULT NULL,\n" +
                        "  `attack` int(11) DEFAULT NULL,\n" +
                        "  `skill` int(11) DEFAULT NULL,\n" +
                        "  `difficulty` int(11) DEFAULT NULL,\n "+
                         "  `like` int(11) DEFAULT NULL)" ;
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE if not exists "
                + EQUIPMENT_TABLE
                + " ( equipmentId Integer NOT NULL PRIMARY KEY, equipName String, subEquipment Integer, salesPrice Integer, " +
                "totalPrice Integer, description String, skill String, image String)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE `skill` (\n" +
                "  `skill_id` int(11) DEFAULT NULL PRIMARY KEY,\n" +
                "  `hero_id` int(11) DEFAULT NULL,\n" +
                "  `name` TEXT DEFAULT NULL,\n" +
                "  `cool` int(11) DEFAULT NULL,\n" +
                "  `waste` int(11) DEFAULT NULL,\n" +
                "  `description` TEXT ,\n" +
                "  `tips` TEXT ,\n" +
                "  `img_url` TEXT DEFAULT NULL\n" +
                ")";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE `hero_equip` (\n" +
                "  `hero_id` int(11) NOT NULL,\n" +
                "  `equip_ids1` TEXT DEFAULT NULL,\n" +
                "  `tips1` text ,\n" +
                "  `equip_ids2` TEXT DEFAULT NULL,\n" +
                "  `tips2` text ,\n" +
                "  PRIMARY KEY (`hero_id`)\n" +
                ")";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE `hero_inscription` (\n" +
                "  `hero_id` int(11) NOT NULL,\n" +
                "  `inscription_ids` String,\n" +
                "  `tips` String ,\n" +
                "  PRIMARY KEY (`hero_id`)\n" +
                ")";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE `inscription` (\n" +
                "  `inscription_id` int(11) NOT NULL PRIMARY KEY,\n" +
                "  `type` TEXT NOT NULL,\n" +
                "  `grade` int(11) NOT NULL,\n" +
                "  `name` TEXT NOT NULL,\n" +
                "  `description` TEXT NOT NULL,\n" +
                "  `img_url` TEXT DEFAULT NULL\n" +
                ")";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE `imagetable`(\n" +
                " `image_id` int(11) NOT NULL PRIMARY KEY,\n"+
                " `image` BOLB NOT NULL \n"+
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertImage(int id,byte[]img){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("image_id", id);
        cv.put("image", img);
        db.insert(IMAGE_TABLE,null,cv);
        db.close();
    }

    public byte[] queryImage(String id, int type){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "image_id = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(IMAGE_TABLE,null,selection, selectionArgs,null,null,null);
        if(cursor.getCount()!=0){
            cursor.moveToNext();
            byte[] in = cursor.getBlob(cursor.getColumnIndex("image"));
            cursor.close();
            return in;
        }else {
            cursor.close();
            String url = "";
            switch (type) {
                case 0://英雄id
                    url = "http://game.gtimg.cn/images/yxzj/img201606/heroimg/"+id+"/"+id+".jpg";
                    break;
                case 1://技能id
                    url = "http://game.gtimg.cn/images/yxzj/img201606/heroimg/"+id.substring(0,id.length()-1)+"/"+id+"0.png";
                    break;
                case 2://英雄原画，英雄id
                    url = "http://game.gtimg.cn/images/yxzj/img201606/skin/hero-info/"+id.substring(1,id.length())+"/"+id.substring(1,id.length())+"-bigskin-1.jpg";
                    break;
                case 3://装备,装备id
                    url = "http://game.gtimg.cn/images/yxzj/img201606/itemimg/"+id+".jpg";
                    break;
                case 4://铭文，铭文id
                    url = "http://game.gtimg.cn/images/yxzj/img201606/mingwen/"+id.substring(1,id.length())+".png";
                    break;
            }
            try {
                Bitmap bitmap = getBitmap(url);
                if(bitmap==null){
                    return null;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                insertImage(Integer.parseInt(id),baos.toByteArray());
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Hero> loadAllHero(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(HEROS_TABLE,null,null,null,null,null,null);
        List<String> map = new ArrayList<>();
        List<Hero>heroes = new ArrayList<>();
        map.add("战士");
        map.add("法师");
        map.add("坦克");
        map.add("刺客");
        map.add("射手");
        map.add("辅助");
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Integer heroid = cursor.getInt(cursor.getColumnIndex("hero_id"));
                String heroname = cursor.getString(cursor.getColumnIndex("name"));
                String herooccupation = map.get(cursor.getInt(cursor.getColumnIndex("hero_type"))-1);
                if(!cursor.isNull(cursor.getColumnIndex("hero_type2"))){
                    herooccupation += map.get(cursor.getInt(cursor.getColumnIndex("hero_type2"))-1);
                }
                String heroposition = "中单";
                String herobackgroundStory = "都是被召唤出来的";
                List<Hero.Skill> heroskills = new ArrayList<>();
                List<Equipment> heroequipments = new ArrayList<>();
                List<Inscription> heroinscriptions = new ArrayList<>();
                String herotype = "远程物理";
                List<Integer> heroabilities = new ArrayList<>();

                heroskills = querySkillsByHeroId(heroid.toString());
                heroequipments =queryHeroEquipmentsByHeroId(heroid);


                String Etip = queryHeroEquipmentsTipByHeroId(heroid);

                heroinscriptions = queryInscriptionsByHeroId(heroid);
                String Itip = queryHeroInscriptionsTipByHeroId(heroid);


                heroabilities.add(cursor.getInt(cursor.getColumnIndex("live")));
                heroabilities.add(cursor.getInt(cursor.getColumnIndex("attack")));
                heroabilities.add(cursor.getInt(cursor.getColumnIndex("skill")));
                heroabilities.add(cursor.getInt(cursor.getColumnIndex("difficulty")));
                heroabilities.add(cursor.getInt(cursor.getColumnIndex("like")));
                byte[] icon = queryImage(Integer.toString(heroid),HERO_IMAGE_TYPE);
                Hero newhero = new Hero(heroid,heroname,herooccupation,heroposition,herobackgroundStory,heroskills,heroequipments,heroinscriptions,herotype,heroabilities,icon);
                newhero.setEquipmentsTip(Etip);
                newhero.setInscriptionsTip(Itip);
                heroes.add(newhero);
            }
        }
        cursor.close();
        return heroes;
    }

    public Hero queryHeroById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "hero_id = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(HEROS_TABLE, null, selection,selectionArgs,null,null,null);
        Hero _hero = new Hero();
        if(cursor.getCount()!=0) {
            cursor.moveToFirst();
            _hero.setId(cursor.getInt(cursor.getColumnIndex("hero_id")));
        }
        cursor.close();
        return _hero;
    }

    public List<Hero.Skill> querySkillsByHeroId(String id){
        List<Hero.Skill> skills = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "hero_id = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(SKILLS_TABLE, null, selection,selectionArgs,null,null,null);
        if(cursor.getCount()!=0) {
            int i = 1;
            while(cursor.moveToNext()){
                Integer skillid = cursor.getInt(cursor.getColumnIndex("skill_id"));
                String skillname = cursor.getString(cursor.getColumnIndex("name"));
                String skillposition = Integer.toString(i);
                String skilldescription = cursor.getString(cursor.getColumnIndex("description"));
                Integer skillcool = cursor.getInt(cursor.getColumnIndex("cool"));
                Integer skillwaste = cursor.getInt(cursor.getColumnIndex("waste"));
                String skilltips = cursor.getString(cursor.getColumnIndex("tips"));
                byte[] image = queryImage(Integer.toString(skillid),SKILLS_IMAGE_TYPE);
                Hero.Skill newskill = new Hero.Skill(skillid,skillname,skillposition,skilldescription,skillcool,skillwaste,skilltips,image);
                i++;
                skills.add(newskill);
            }
        }
        cursor.close();
        return skills;
    }

    public List<Inscription> queryInscriptionsByHeroId(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        List<Inscription> inscription = new ArrayList<>();
        String selection = " hero_id = ?";
        String[] selectionArgs = {id.toString()};
        Cursor c = db.query(HERO_INSCRIPTION_TABLE, null, selection,selectionArgs,null,null,null);
        String inscriptions=null;
        String []str_ids=null;
        if(c.moveToNext()){
            inscriptions = (String) c.getString(1);
        }
        if(inscriptions !=null){
            str_ids =  inscriptions.split("\\,");
        }
        for(int i=0;i<str_ids.length;i++){
            Integer k =Integer.parseInt(str_ids[i]);
            Inscription e  = queryInscritionById(k);
            inscription.add(e);
        }

        c.close();
        db.close();
        return inscription;
    }
    public List<Equipment> queryHeroEquipmentsByHeroId(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        List<Equipment> equipments = new ArrayList<>();
        String selection = " hero_id = ?";
        String[] selectionArgs = {id.toString()};
        Cursor c = db.query(HERO_EQUIP, null, selection,selectionArgs,null,null,null);
        String equip_ids=null;
        String []str_ids=null;
        if(c.moveToNext()){
            equip_ids = (String) c.getString(1);
         }
         if(equip_ids !=null){
            str_ids =  equip_ids.split("\\,");
         }
         for(int i=0;i<str_ids.length;i++){
             Integer k =Integer.parseInt(str_ids[i]);
             Equipment e  = queryEquipmentById(k);
             equipments.add(e);
         }

        c.close();
        db.close();
        return  equipments;
    }

    public String queryHeroEquipmentsTipByHeroId(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        List<Equipment> equipments = new ArrayList<>();
        String selection = " hero_id = ?";
        String[] selectionArgs = {id.toString()};
        Cursor c = db.query(HERO_EQUIP, null, selection,selectionArgs,null,null,null);
        String tip=null;
        if(c.moveToNext()){
            tip = (String) c.getString(2);
        }



        c.close();
        db.close();
        return tip ;
    }

    public String queryHeroInscriptionsTipByHeroId(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        List<Equipment> equipments = new ArrayList<>();
        String selection = " hero_id = ?";
        String[] selectionArgs = {id.toString()};
        Cursor c = db.query(HERO_INSCRIPTION_TABLE, null, selection,selectionArgs,null,null,null);
        String tip=null;
        if(c.moveToNext()){
            tip = (String) c.getString(2);
        }



        c.close();
        db.close();
        return tip ;
    }


    public Equipment queryEquipmentById(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        Equipment e = null;
        String selection = " equipmentId = ?";
        String[] selectionArgs = {id.toString()};
        Cursor c = db.query(EQUIPMENT_TABLE, null, selection,selectionArgs,null,null,null);
        if(c.moveToNext()){
            byte[] image = queryImage(Integer.toString(c.getInt(0)),EQUIPMENT_IMAGE_TYPE);
            e = new Equipment(c.getString(1),c.getString(5),image,c.getInt(0),c.getInt(2), c.getInt(3),c.getString(6));
        }
        c.close();
        db.close();
        return e;
    }

    public boolean isEquipmentEmpty(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(EQUIPMENT_TABLE, null, null, null,null,null,null);
        if(c.getCount()==0){
            db.close();
            c.close();
            return true;
        }
        c.close();
        db.close();
        return false;
    }
    public List<Equipment> queryAllEquipment(){
        SQLiteDatabase db = getReadableDatabase();
        List<Equipment> list = null;
        Cursor c = db.query(EQUIPMENT_TABLE, null, null, null,null,null,null);
        if(c.getCount()==0){
            db.close();
            c.close();
            return null;
        }
        list = new ArrayList<Equipment>();
        while(c.moveToNext()){
            byte[] image = queryImage(Integer.toString(c.getInt(0)),EQUIPMENT_IMAGE_TYPE);
            Equipment e = new Equipment(c.getString(1),c.getString(5),image,
                    c.getInt(0),c.getInt(2), c.getInt(3),c.getString(6));
            list.add(e);
        }
        c.close();
        db.close();
        return list;
    }
    public List<Equipment> getEquipmentByType(Integer id){
        SQLiteDatabase db = getWritableDatabase();
        List<Equipment> list = null;
        String select = "subEquipment = ?";
        String[] selection = {id.toString()};
        Cursor c = db.query(EQUIPMENT_TABLE, null, select, selection,null,null,null);
        if(c.getCount()==0){
            db.close();
            c.close();
            return null;
        }
        list = new ArrayList<Equipment>();
        while(c.moveToNext()){
            byte[] image = queryImage(Integer.toString(c.getInt(0)),EQUIPMENT_IMAGE_TYPE);
            Equipment e = new Equipment(c.getString(1),c.getString(5),image,c.getInt(0),c.getInt(2), c.getInt(3),c.getString(6));
            list.add(e);
        }
        c.close();
        db.close();
        return list;
    }

    public List<Inscription> queryAllInscriptions(){
        SQLiteDatabase db = getReadableDatabase();
        List<Inscription> Inscriptions = null;
        Cursor cursor = db.query(INSCRIPTION_TABLE,null,null,null,null,null,null);
        if(cursor.getCount()!=0){
            Inscriptions = new ArrayList<>();
            while (cursor.moveToNext()){
                String id = Integer.toString(cursor.getInt(0));
                byte[] image = queryImage(INSCRIPTION_IMAGE_TYPE+id,INSCRIPTION_IMAGE_TYPE);
                Inscription inscription = new Inscription(cursor.getInt(0),cursor.getString(1),
                        cursor.getInt(2),cursor.getString(3),cursor.getString(4),image);
                Inscriptions.add(inscription);
            }
        }
        cursor.close();
        db.close();
        return Inscriptions;
    }


    public Bitmap getBitmap(String path) throws IOException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            conn.disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public Inscription queryInscritionById(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        Inscription inscription = null;
        String selection = " inscription_id = ?";
        String[] selectionArgs = {id.toString()};
        Cursor cursor = db.query(INSCRIPTION_TABLE,null,selection,selectionArgs,null,null,null);
        if(cursor.moveToNext()){
            String Id = Integer.toString(cursor.getInt(0));
            byte[] image = queryImage(INSCRIPTION_IMAGE_TYPE+Id,INSCRIPTION_IMAGE_TYPE);
            inscription = new Inscription(cursor.getInt(0),cursor.getString(1),
                    cursor.getInt(2),cursor.getString(3),cursor.getString(4),image);
            cursor.close();
            db.close();
            return inscription;
        }
        cursor.close();
        db.close();
        return inscription;
    }


    public void initDB(){
        initHero();
        initEquipment();
        initInscription();
        return;
    }

    public void initHero(){
        SQLiteDatabase db = getWritableDatabase();
        String[]insert = {"INSERT INTO `hero` VALUES ('105', '廉颇', '10', '0', '3', null, '正义爆轰|地狱岩魂', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/105/105.jpg', '100', '30', '40', '30', '50');",
                "INSERT INTO `hero` VALUES ('106', '小乔', null, '0', '2', null, '恋之微风|万圣前夜|天鹅之梦|纯白花嫁|缤纷独角兽', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/106/106.jpg', '20', '10', '80', '30', '50');",
                "INSERT INTO `hero` VALUES ('107', '赵云', null, '0', '1', '4', '苍天翔龙|忍●炎影|未来纪元|皇家上将|嘻哈天王|白执事|引擎之心', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/107/107.jpg', '60', '60', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('108', '墨子', null, '0', '2', '3', '和平守望|金属风暴|龙骑士|进击墨子号', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/108/108.jpg', '50', '40', '50', '60', '50');",
                "INSERT INTO `hero` VALUES ('109', '妲己', '11', '0', '2', null, '魅惑之狐|女仆咖啡|魅力维加斯|仙境爱丽丝|少女阿狸|热情桑巴', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/109/109.jpg', '20', '10', '80', '20', '50');",
                "INSERT INTO `hero` VALUES ('110', '嬴政', null, '0', '2', null, '王者独尊|摇滚巨星|暗夜贵公子|优雅恋人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/110/110.jpg', '30', '40', '100', '60', '50');",
                "INSERT INTO `hero` VALUES ('111', '孙尚香', null, '0', '5', null, '千金重弩|火炮千金|水果甜心|蔷薇恋人|杀手不太冷|末日机甲|沉稳之力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/111/111.jpg', '30', '80', '50', '60', '50');",
                "INSERT INTO `hero` VALUES ('112', '鲁班七号', '11', '0', '5', null, '机关造物|木偶奇遇记|福禄兄弟|电玩小子|星空梦想', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/112/112.jpg', '10', '100', '30', '40', '50');",
                "INSERT INTO `hero` VALUES ('113', '庄周', null, '0', '6', '3', '逍遥幻梦|鲤鱼之梦|蜃楼王|云端筑梦师', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/113/113.jpg', '80', '20', '40', '20', '50');",
                "INSERT INTO `hero` VALUES ('114', '刘禅', null, '0', '3', null, '暴走机关|英喵野望|绅士熊喵', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/114/114.jpg', '70', '30', '50', '30', '50');",
                "INSERT INTO `hero` VALUES ('115', '高渐离', null, '0', '2', null, '叛逆吟游|金属狂潮|死亡摇滚', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/115/115.jpg', '50', '40', '90', '40', '50');",
                "INSERT INTO `hero` VALUES ('116', '阿轲', null, '0', '4', null, '信念之刃|爱心护理|暗夜猫娘|致命风华', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/116/116.jpg', '30', '100', '40', '60', '50');",
                "INSERT INTO `hero` VALUES ('117', '钟无艳', null, '0', '1', '3', '野蛮之锤|生化警戒|王者之锤|海滩丽影', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/117/117.jpg', '70', '50', '70', '40', '50');",
                "INSERT INTO `hero` VALUES ('118', '孙膑', null, '0', '6', '2', '逆流之时|未来旅行|天使之翼|妖精王', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/118/118.jpg', '20', '10', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('119', '扁鹊', null, '0', '2', '6', '善恶怪医|救世之瞳|化身博士|炼金王', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/119/119.jpg', '50', '40', '30', '40', '50');",
                "INSERT INTO `hero` VALUES ('120', '白起', null, '0', '3', null, '最终兵器|白色死神|狰', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/120/120.jpg', '80', '30', '40', '40', '50');",
                "INSERT INTO `hero` VALUES ('121', '芈月', null, '0', '2', '3', '永恒之月|红桃皇后|大秦宣太后|重明', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/121/121.jpg', '60', '50', '70', '50', '50');",
                "INSERT INTO `hero` VALUES ('123', '吕布', null, '0', '1', '3', '无双之魔|圣诞狂欢|天魔缭乱|末日机甲', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/123/123.jpg', '60', '60', '30', '40', '50');",
                "INSERT INTO `hero` VALUES ('124', '周瑜', null, '0', '2', null, '铁血都督|海军大将|真爱至上', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/124/124.jpg', '30', '40', '90', '70', '50');",
                "INSERT INTO `hero` VALUES ('125', '元歌', null, '1', '4', null, '无间傀儡|午夜歌剧院', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/125/125.jpg', '10', '50', '90', '100', '50');",
                "INSERT INTO `hero` VALUES ('126', '夏侯惇', null, '0', '3', '1', '不羁之风|战争骑士|乘风破浪', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/126/126.jpg', '70', '40', '50', '40', '50');",
                "INSERT INTO `hero` VALUES ('127', '甄姬', null, '0', '2', null, '洛神降临|冰雪圆舞曲|花好人间|游园惊梦', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/127/127.jpg', '10', '10', '70', '40', '50');",
                "INSERT INTO `hero` VALUES ('128', '曹操', null, '0', '1', null, '鲜血枭雄|超能战警|幽灵船长|死神来了|烛龙', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/128/128.jpg', '60', '60', '50', '40', '50');",
                "INSERT INTO `hero` VALUES ('129', '典韦', null, '0', '1', null, '狂战士|黄金武士|穷奇', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/129/129.jpg', '60', '60', '30', '10', '50');",
                "INSERT INTO `hero` VALUES ('130', '宫本武藏', null, '0', '1', null, '剑圣|鬼剑武藏|未来纪元|万象初新|地狱之眼|霸王丸', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/130/130.jpg', '50', '70', '40', '50', '50');",
                "INSERT INTO `hero` VALUES ('131', '李白', null, '0', '4', '1', '青莲剑仙|范海辛|千年之狐|凤求凰', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/131/131.jpg', '40', '70', '60', '90', '50');",
                "INSERT INTO `hero` VALUES ('132', '马可波罗', null, '0', '5', null, '远游之枪|激情绿茵|逐梦之星', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/132/132.jpg', '30', '60', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('133', '狄仁杰', '11', '0', '5', null, '断案大师|锦衣卫|魔术师|超时空战士|阴阳师', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/133/133.jpg', '40', '90', '30', '20', '50');",
                "INSERT INTO `hero` VALUES ('134', '达摩', null, '0', '1', '3', '拳僧|大发明家|拳王', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/134/134.jpg', '70', '50', '20', '50', '50');",
                "INSERT INTO `hero` VALUES ('135', '项羽', null, '0', '3', null, '霸王|帝国元帅|苍穹之光|海滩派对|职棒王牌|霸王别姬', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/135/135.jpg', '90', '40', '50', '30', '50');",
                "INSERT INTO `hero` VALUES ('136', '武则天', null, '0', '2', null, '女帝|东方不败|海洋之心', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/136/136.jpg', '20', '10', '100', '60', '50');",
                "INSERT INTO `hero` VALUES ('139', '老夫子', null, '0', '1', null, '万古长明|潮流仙人|圣诞老人|功夫老勺', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/139/139.jpg', '60', '60', '40', '30', '50');",
                "INSERT INTO `hero` VALUES ('140', '关羽', null, '0', '1', '3', '一骑当千|天启骑士|冰锋战神|龙腾万里', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/140/140.jpg', '60', '60', '60', '80', '50');",
                "INSERT INTO `hero` VALUES ('141', '貂蝉', null, '0', '2', '4', '绝世舞姬|异域舞娘|圣诞恋歌|逐梦之音|仲夏夜之梦', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/141/141.jpg', '40', '20', '70', '60', '50');",
                "INSERT INTO `hero` VALUES ('142', '安琪拉', null, '0', '2', null, '暗夜萝莉|玩偶对对碰|魔法小厨娘|心灵骇客', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/142/142.jpg', '40', '10', '80', '40', '50');",
                "INSERT INTO `hero` VALUES ('144', '程咬金', null, '0', '3', '1', '热烈之斧|爱与正义|星际陆战队|华尔街大亨', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/144/144.jpg', '90', '50', '50', '10', '50');",
                "INSERT INTO `hero` VALUES ('146', '露娜', null, '0', '1', '2', '月光之女|哥特玫瑰|绯红之刃|紫霞仙子', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/146/146.jpg', '50', '40', '70', '90', '50');",
                "INSERT INTO `hero` VALUES ('148', '姜子牙', null, '0', '2', '6', '太古魔导|时尚教父', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/148/148.jpg', '10', '20', '70', '30', '50');",
                "INSERT INTO `hero` VALUES ('149', '刘邦', null, '0', '3', '6', '双面君主|圣殿之光|德古拉伯爵', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/149/149.jpg', '80', '40', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('150', '韩信', null, '0', '4', '1', '国士无双|街头霸王|教廷特使|白龙吟|逐梦之影', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/150/150.jpg', '20', '70', '30', '80', '50');",
                "INSERT INTO `hero` VALUES ('152', '王昭君', '10', '0', '2', null, '冰雪之华|精灵公主|偶像歌手|凤凰于飞|幻想奇妙夜', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/152/152.jpg', '40', '40', '80', '40', '50');",
                "INSERT INTO `hero` VALUES ('153', '兰陵王', null, '0', '4', null, '暗影刀锋|隐刃|暗隐猎兽者', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/153/153.jpg', '40', '80', '40', '50', '50');",
                "INSERT INTO `hero` VALUES ('154', '花木兰', null, '0', '1', '4', '传说之刃|剑舞者|兔女郎|水晶猎龙者|青春决赛季', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/154/154.jpg', '50', '70', '50', '80', '50');",
                "INSERT INTO `hero` VALUES ('156', '张良', null, '0', '2', null, '言灵之书|天堂福音|一千零一夜', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/156/156.jpg', '30', '10', '80', '60', '50');",
                "INSERT INTO `hero` VALUES ('157', '不知火舞', null, '0', '2', '4', '明媚烈焰', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/157/157.jpg', '30', '30', '80', '80', '50');",
                "INSERT INTO `hero` VALUES ('162', '娜可露露', null, '0', '4', null, '鹰之守护', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/162/162.jpg', '30', '80', '60', '40', '50');",
                "INSERT INTO `hero` VALUES ('163', '橘右京', null, '0', '4', '1', '神梦一刀', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/163/163.jpg', '50', '60', '50', '50', '50');",
                "INSERT INTO `hero` VALUES ('166', '亚瑟', '11', '0', '1', '3', '圣骑之力|死亡骑士|狮心王|心灵战警', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/166/166.jpg', '80', '30', '50', '20', '50');",
                "INSERT INTO `hero` VALUES ('167', '孙悟空', null, '0', '1', '4', '齐天大圣|地狱火|西部大镖客|美猴王|至尊宝', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/167/167.jpg', '50', '80', '50', '40', '50');",
                "INSERT INTO `hero` VALUES ('168', '牛魔', '10', '0', '3', '6', '精英酋长|西部大镖客|制霸全明星', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/168/168.jpg', '70', '40', '50', '50', '50');",
                "INSERT INTO `hero` VALUES ('169', '后羿', null, '0', '5', null, '半神之弓|精灵王|阿尔法小队|辉光之辰', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/169/169.jpg', '30', '80', '40', '30', '50');",
                "INSERT INTO `hero` VALUES ('170', '刘备', null, '0', '1', null, '仁德义枪|万事如意|纽约教父|汉昭烈帝', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/170/170.jpg', '60', '60', '40', '40', '50');",
                "INSERT INTO `hero` VALUES ('171', '张飞', null, '0', '3', '6', '禁血狂兽|五福同心|乱世虎臣', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/171/171.jpg', '100', '40', '50', '60', '50');",
                "INSERT INTO `hero` VALUES ('173', '李元芳', null, '0', '5', null, '王都密探|特种部队|黑猫爱糖果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/173/173.jpg', '30', '80', '50', '50', '50');",
                "INSERT INTO `hero` VALUES ('174', '虞姬', '10', '0', '5', null, '森之风灵|加勒比小姐|霸王别姬|凯尔特女王', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/174/174.jpg', '40', '80', '50', '40', '50');",
                "INSERT INTO `hero` VALUES ('175', '钟馗', null, '0', '2', '1', '虚灵城判|地府判官', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/175/175.jpg', '50', '40', '70', '50', '50');",
                "INSERT INTO `hero` VALUES ('176', '杨玉环', null, '0', '2', '6', '风华霓裳|霓裳曲', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/176/176.jpg', '30', '30', '90', '50', '50');",
                "INSERT INTO `hero` VALUES ('177', '成吉思汗', null, '0', '5', null, '苍狼末裔|维京掠夺者', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/177/177.jpg', '30', '80', '30', '50', '50');",
                "INSERT INTO `hero` VALUES ('178', '杨戬', null, '0', '1', null, '根源之目|埃及法老|永曜之星', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/178/178.jpg', '60', '60', '50', '50', '50');",
                "INSERT INTO `hero` VALUES ('179', '女娲', '10', '0', '2', null, '至高创世|尼罗河女神', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/179/179.jpg', '30', '10', '90', '70', '50');",
                "INSERT INTO `hero` VALUES ('180', '哪吒', '10', '0', '1', null, '桀骜炎枪|三太子|逐梦之翼', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/180/180.jpg', '80', '30', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('182', '干将莫邪', null, '0', '2', null, '淬命双剑|第七人偶', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/182/182.jpg', '40', '30', '80', '70', '50');",
                "INSERT INTO `hero` VALUES ('183', '雅典娜', null, '0', '1', null, '圣域余晖|战争女神|冰冠公主|神奇女侠', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/183/183.jpg', '70', '60', '40', '60', '50');",
                "INSERT INTO `hero` VALUES ('184', '蔡文姬', null, '0', '6', null, '天籁弦音|蔷薇王座|舞动绿茵', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/184/184.jpg', '50', '10', '80', '30', '50');",
                "INSERT INTO `hero` VALUES ('186', '太乙真人', null, '0', '6', '3', '炼金大师|圆桌骑士|饕餮', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/186/186.jpg', '50', '10', '60', '70', '50');",
                "INSERT INTO `hero` VALUES ('187', '东皇太一', null, '0', '3', null, '噬灭日蚀|东海龙王', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/187/187.jpg', '80', '20', '50', '40', '50');",
                "INSERT INTO `hero` VALUES ('189', '鬼谷子', null, '0', '6', null, '万物有灵|阿摩司公爵', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/189/189.jpg', '50', '20', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('190', '诸葛亮', null, '0', '2', null, '绝代智谋|星航指挥官|黄金分割率|武陵仙君', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/190/190.jpg', '30', '10', '80', '60', '50');",
                "INSERT INTO `hero` VALUES ('191', '大乔', null, '0', '6', null, '沧海之曜|伊势巫女', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/191/191.jpg', '10', '10', '60', '60', '50');",
                "INSERT INTO `hero` VALUES ('192', '黄忠', null, '0', '5', null, '燃魂重炮|芝加哥教父', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/192/192.jpg', '40', '90', '40', '40', '50');",
                "INSERT INTO `hero` VALUES ('193', '铠', null, '0', '1', '3', '破灭刃锋|龙域领主|曙光守护者', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/193/193.jpg', '70', '70', '40', '20', '50');",
                "INSERT INTO `hero` VALUES ('194', '苏烈', null, '0', '3', '1', '不屈铁壁|爱与和平', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/194/194.jpg', '70', '40', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('195', '百里玄策', null, '0', '4', null, '嚣狂之镰|威尼斯狂欢', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/195/195.jpg', '40', '80', '40', '100', '50');",
                "INSERT INTO `hero` VALUES ('196', '百里守约', null, '0', '5', '4', '静谧之眼|绝影神枪|特工魅影', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/196/196.jpg', '20', '70', '40', '70', '50');",
                "INSERT INTO `hero` VALUES ('197', '弈星', null, '0', '2', null, '天元之弈|踏雪寻梅', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/197/197.jpg', '40', '30', '90', '60', '50');",
                "INSERT INTO `hero` VALUES ('198', '梦奇', null, '0', '3', '2', '入梦之灵|美梦成真', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/198/198.jpg', '80', '40', '40', '40', '50');",
                "INSERT INTO `hero` VALUES ('199', '公孙离', null, '0', '5', null, '幻舞玲珑|花间舞', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/199/199.jpg', '20', '80', '30', '70', '50');",
                "INSERT INTO `hero` VALUES ('501', '明世隐', null, '0', '6', null, '灵魂劫卜|占星术士', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/501/501.jpg', '60', '30', '50', '70', '50');",
                "INSERT INTO `hero` VALUES ('502', '裴擒虎', null, '0', '4', '1', '六合虎拳|街头霸王|梅西', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/502/502.jpg', '40', '80', '50', '70', '50');",
                "INSERT INTO `hero` VALUES ('503', '狂铁', '10', '0', '1', null, '战车意志|命运角斗场', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/503/503.jpg', '60', '60', '60', '50', '50');",
                "INSERT INTO `hero` VALUES ('504', '米莱狄', null, '0', '2', null, '筑城者|精准探案法', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/504/504.jpg', '20', '10', '80', '40', '50');"
        };
        String[] insertSkill = {"INSERT INTO `skill` VALUES ('1140', '114', '磁力屏障', '0', '0', '被动：刘禅可对机关释放技能造成等量的伤害，并且刘禅的控制技能可对机关造成干扰效果，持续1秒；当刘禅技能对机关造成还伤害时会掠夺零件为自身回复200~396点生命值，回复效果随英雄等级成长', '被动让刘禅能更快的摧毁敌方的防御塔和水晶', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/114/11400.png');",
                "INSERT INTO `skill` VALUES ('1141', '114', '小霸王护盾', '12', '70', '刘禅立即重置普攻并开启护盾向前发起冲锋，吸收600/840/1080/1320/1560/1800点伤害同时提升自身25%移动速度持续3秒，并强化下一次普通攻击，造成350/400/450/500/550/600（+122%物理加成）点物理伤害并击飞敌人1秒', '几乎每次开团都是由刘禅的1技能开启的，开启护盾冲锋将敌人击飞，留住敌人，辅助队友将其击杀，当然这个技能在面对敌方gank或追击时也可用来逃跑', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/114/11410.png');",
                "INSERT INTO `skill` VALUES ('1142', '114', '机关魔爪', '8', '65', '刘禅操纵机关熊猫向指定方向伸出魔爪进行攻击，对范围内的敌人造成400/480/560/640/720/800（+100%物理加成）点物理伤害并且会将范围内敌人眩晕1秒', '2技能控制指定方向范围上的敌人，为队友创造输出条件', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/114/11420.png');",
                "INSERT INTO `skill` VALUES ('1143', '114', '暴走熊猫', '40', '140', '刘禅操纵机关熊猫展开双臂持续旋转攻击附近敌人，持续3秒，对方范围内的敌人每0.5秒造成240/320/400（+75%物理加成）点物理伤害。', '大招是AOE伤害，范围较大，输出不高但威慑力十足.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/114/11430.png');",
                "INSERT INTO `skill` VALUES ('1150', '115', '哀歌', '0', '0', '被动：高渐离的普通攻击或释放技能可视作一次演奏，每四次演奏后的下一次普通攻击将会向目标弹奏出带有击穿效果的能量和弦，造成300（+100%法术加成）的法术伤害', '能量和弦具有穿透效果，同时拥有物理与法术加成.这个效果不管在前后期都是不错的消耗方式', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/115/11500.png');",
                "INSERT INTO `skill` VALUES ('1151', '115', '狂歌', '7', '80', '高渐离弹奏出音符，锁定附近至多2名敌人造成350/410/470/530/590/650（+47%法术加成）点法术伤害；音符命中敌人后，会向附近的敌人进行弹射，音符至多弹射2次；当多个音符命中同一目标时，从第二个音符开始将只造成30%伤害', '技能可同时攻击两个敌方单位，并且施法范围较大，对线或团战前都可以用来消耗敌人，前期伤害也是较为可观的.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/115/11510.png');",
                "INSERT INTO `skill` VALUES ('1152', '115', '离歌', '10', '70', '高渐离震慑琴弦，对附近敌人造成500/560/620/680/740/800（+65%法术加成）点法术伤害并减少其35%/40%/45%/50%/55%/60%移动速度，持续2秒', 'AOE技能，造成伤害并减速，能留住敌人，配合大招使用效果更佳.但需注意，此技能范围较小，注意与敌军距离，靠近后再释放', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/115/11520.png');",
                "INSERT INTO `skill` VALUES ('1153', '115', '魔音贯耳', '45', '140', '高渐离释放激情，每0.5秒进行一次演奏，对附近敌人造成200/250/300（+15%法术加成）点法术伤害，持续5秒；演奏持续时自身会减少15%所受到的伤害，并且附近每个敌方英雄还将减少高渐离5%所收到的伤害；并且在演奏开始时自身会增加70%持续衰减的移动速度，持续3秒', '持续性的AOE输出加上免伤效果和移动速度加成，这个技能在后期有着高额的法术伤害和极强的机动性，是非常棒的团战切后排的技能.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/115/11530.png');",
                "INSERT INTO `skill` VALUES ('1180', '118', '时间沙漏', '0', '0', '被动：孙膑释放技能会增加20%移动速度，持续1秒', '施放技能会短暂提升移动速度，这无疑提升了孙膑的生存能力，一定程度影响敌人的攻击节奏', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/118/11800.png');",
                "INSERT INTO `skill` VALUES ('1181', '118', '时空爆弹', '8', '60', '孙膑向指定方向投掷一枚炸弹，并附带着在目标身上，持续3秒后爆炸，附着目标时造成180/225/270/315/360/405（+18%法术加成）点法术伤害，爆炸对范围内敌人造成540/675/810/945/1080/1215（+54%法术加成）点法术伤害并减少其40%移动速度，持续2秒。', '这是孙膑一个重要的团控技能，技能范围较远，可以用来很好的留人或撤退.该技能还能降低敌方魔抗，对于我方法师来说，放大了他们的输出能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/118/11810.png');",
                "INSERT INTO `skill` VALUES ('1182', '118', '时之波动', '12', '60', '孙膑引导时空之力，使得范围内友军增加30%/36%/42%/48%/54%/60%移动速度和20%冷却缩短，持续2秒，持续时间结束后会时光倒流，并返还期间所受到40%的伤害', '为附近友军补充承受伤害的护盾，并提高移动速度，提升友军的输出能力，这个技能无论是追击或者掩护队友撤退都很强，这是孙膑作为法师辅助最有特点的技能之一', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/118/11820.png');",
                "INSERT INTO `skill` VALUES ('1183', '118', '时光流逝', '45', '100', '孙膑向指定方向投掷一枚强化炸弹，附着敌人或飞行到最大距离后展开力场，展开时会对范围内敌人造成500/625/750（+45%法术加成）点法术伤害并减少其50%移动速度，同时敌人还会被沉默1秒；力场将持续5秒，处于力场中的敌人会减少25%冷却的恢复速度，5秒后会再次对范围内敌人造成1500/1875/2250（+135%法术加成）点法术伤害。', '这个技能范围较大，团战时如果命中多个敌人，能沉默并减速敌方对手，变相降低了敌人的输出，为我方英雄创造非常好的输出条件.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/118/11830.png');",
                "INSERT INTO `skill` VALUES ('1190', '119', '恶德医疗', '0', '0', '被动：扁鹊的普通攻击和技能都会附带剧毒，每秒造成25（+5%法术加成）点法术伤害，并叠加1层毒药印记，当友方英雄受到善恶诊断的治疗时，每秒回复5（+2%法术加成）点生命值，并叠加1层回复印记；毒药印记和恢复印记最多可叠加至多5层，持续7秒', '该技能可以在技能或普攻后有持续伤害，所以线上消耗和换血都是一个不错的选择，在与其对拼时，不要等到残血再撤退，否则有可能被持续伤害的效果击杀', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/119/11900.png');",
                "INSERT INTO `skill` VALUES ('1191', '119', '致命灵药', '2', '40', '扁鹊朝指定目标范围扔出致命毒药瓶，毒药瓶触地后破碎，毒液会残留原地，持续4秒，毒液每秒会对范围内的敌人造成70/76/82/88/94/100（+12%法术加成）点法术伤害并减少其30%移动速度，持续2秒；扁鹊每15/14.4/13.8/13.2/12.6/12秒可制作1瓶毒药，最多存放3瓶', '伤害不太高，主要的作用还是在于减速，相当于一个软性控制技能，限制敌方的走位', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/119/11910.png');",
                "INSERT INTO `skill` VALUES ('1192', '119', '善恶诊断', '5', '60', '扁鹊朝指定方向释放药剂，对路径上的敌人造成100/120/140/160/180/200（+30%法术加成）点法术伤害，并且对触碰的友军英雄施加恢复印记并回复50/60/70/80/90/100（+15%法术加成）点生命值', '该技能可以对友军和自己进行治疗，在蓝量健康的情况下，可以施放技能来回血，团战中，能给队友形成不错的治疗效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/119/11920.png');",
                "INSERT INTO `skill` VALUES ('1193', '119', '生命主宰', '18', '130', '扁鹊引爆附近所有印记，对敌人造成200/250/300（+40%法术加成）点法术伤害，对友军英雄治疗80/100/120（+20%法术加成）点生命值.每层印记额外增加50%的额外伤害与治疗效果。', '大范围的伤害和回复技能，在被动印记层数较高时，能对敌方造成强力的法术伤害，对友军有很强的治疗效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/119/11930.png');",
                "INSERT INTO `skill` VALUES ('1210', '121', '永生之血', '0', '0', '被动：芈月释放技能会召唤暗影仆从并对附近的敌人进行攻击，每次攻击会造成40（+7%法术加成）点法术伤害并回复芈月20（+3%法术加成）点生命值；召唤暗影仆从时芈月获得30点暗影之力，暗影之力消失时暗影仆从也会立即消失；暗影仆从最多存在6个', '芈月施放技能召唤暗影仆从，合理的召唤并使用仆从可以大大增强自己吸收伤害能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/121/12100.png');",
                "INSERT INTO `skill` VALUES ('1211', '121', '痛苦冲击', '10', '0', '芈月向指定方向释放暗影能量，对路径上的敌人造成200/220/240/260/280/300（+24%法术加成）点法术伤害；在暗影能量飞行的过程中，芈月能够再次使用技能降临在暗影能量的位置，并对范围内敌人造成200/220/240/260/280/300（+24%法术加成）法术伤害；第二段技能命中敌人会为芈月召唤一个暗影仆从', '芈月可以使用这个技能来进行位移和造成伤害，并且在第二段施放前，芈月可以随意走位迷惑对手进行位移', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/121/12110.png');",
                "INSERT INTO `skill` VALUES ('1212', '121', '幻翼仆从', '9', '0', '芈月向指定方向释放暗影锁链，锁链将会和第一个命中的敌人连接，最多持续4秒；连接后每0.5秒将抽取目标的15（+2%法术加成）点生命值、10点物理攻击力与30点法术攻击力，并造成50/60/70/80/90/100（+7%法术加成）点法术伤害；如果连接持续4秒，会为芈月召唤一个暗影仆从；双方超过一定距离会挣脱连接', '芈月利用此技能降低敌方的输出，所以敌方后排英雄要注意通过走位躲避其技能，不然无法输出更多伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/121/12120.png');",
                "INSERT INTO `skill` VALUES ('1213', '121', '暗影之月', '24', '0', '芈月遁入暗影，增加30%移动速度并免疫所有效果，持续2秒；技能开启及结束时都将对范围内的敌人造成400/500/600（+50%法术加成）点法术伤害；如果第二段伤害命中敌人，会为芈月召唤一个暗影仆从；被动：芈月普通攻击得到强化，造成40（+100%物理加成）（+20%法术加成）点法术伤害并减少其10%移动速度，同时增加自身10点暗影之力', '这是芈月高自保能力的保障，增加移速和免疫效果。被动使得芈月普攻得到强化，造成法术伤害和控制效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/121/12130.png');",
                "INSERT INTO `skill` VALUES ('1080', '108', '兼爱非攻', '0', '0', '被动：墨子连续使用普通攻击时，第四次普通攻击将变更为炮击，炮击会造成（+125%法术加成）及（+150%物理加成）的法术伤害，同时将目标击退0.75秒；当墨子施放技能时会获得抵免250（+35%法术加成）点伤害的护盾，持续3秒', '承受敌方攻击时，释放技能形成护盾抵消部分伤害，第四次普攻有物理和法术加成已经击退效果，在血量充足的情况下尽量打出此被动技能造成伤害，击退能有小团控的效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/108/10800.png');",
                "INSERT INTO `skill` VALUES ('1081', '108', '和平漫步', '10', '70', '墨子驾驶机关人向指定方向突进，对路径上的敌人造成250/300/350/400/450/500（+38法术加成）点法术伤害，并将下一次普通攻击变更为炮击', '可利用此技能突进，控制敌人，也可利用此技能进行穿墙逃跑', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/108/10810.png');",
                "INSERT INTO `skill` VALUES ('1082', '108', '机关重炮', '7', '70', '墨子驾驶机关人向指定方向发射一枚炮弹，炮弹命中敌人时会发生范围爆炸造成500/560/620/680/740/800（+65%法术加成）点法术伤害并将范围内敌人眩晕1秒，炮弹爆炸后将留下弹坑持续4秒每0.5秒造成50/56/62/68/74/80（+52%法术加成）点法术伤害', '远程范围控制技能，可用于远距离开团或撤退时控制敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/108/10820.png');",
                "INSERT INTO `skill` VALUES ('1083', '108', '墨守成规', '42', '150', '墨子驾驶机关人展开高能屏障，对触碰到屏障的敌人每0.5秒造成200/250/300（+45%法术加成）点法术伤害并将其晕眩在原地，屏障最多持续3.3秒', '拥有长达5秒的控制时间，但是这段时间内很容易被敌方技能打断控制效果.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/108/10830.png');",
                "INSERT INTO `skill` VALUES ('1070', '107', '龙鸣', '0', '0', '被动：赵云每损失3%最大生命值，就会获得减少1%所受到的伤害', '赵云的生命值越低，免伤效果越强，提升了赵云的战斗生存能力，也使得他更容易形成残血反杀', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/107/10700.png');",
                "INSERT INTO `skill` VALUES ('1071', '107', '惊雷之龙', '7', '40', '赵云执枪向指定方向冲锋，对路径上的敌人造成190/214/238/262/286/310（+100%物理加成）点物理伤害；冲锋后的下一次普通攻击会造成65/74/83/92/101/110（+160%物理加成）点物理伤害并将其减少25%移动速度，持续2秒', '突进技能，利用此技能突进敌方后排或选择撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/107/10710.png');",
                "INSERT INTO `skill` VALUES ('1072', '107', '破云之龙', '8', '50', '赵云向指定方向连续3次刺出龙枪，对指定方向敌人每次造成140/158/176/194/212/230（+60%物理加成）点物理伤害；每次命中英雄后回复35/39/43/47/51/55（+14%物理加成）点生命值', '此技能范围较近，能持续对敌人造成伤害，但很依赖走位和操作技巧', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/107/10720.png');",
                "INSERT INTO `skill` VALUES ('1073', '107', '天翔之龙', '18', '100', '赵云跃向空中，向目标区域发动雷霆一击，对范围内敌人造成370/460/550（+140%物理加成）点物理伤害并击飞1秒；同时标记敌人为感电目标；赵云的普通攻击和技能伤害对感电目标会造成额外30/45/60（+13%物理加成）点法术伤害', '突进技能，有AOE伤害和控制效果，但是技能有一定时间的前摇，所以使用前需要预判好敌方位置', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/107/10730.png');",
                "INSERT INTO `skill` VALUES ('1280', '128', '争霸', '0', '0', '被动：曹操每次施放技能会增加10%攻击速度，最多叠加5层，持续3秒', '这是曹操能够在战斗中打出爆发输出的基础，在被动技能支持下，曹操只需要再拥有足够的冷却缩减，就可以不断的施放纵横天下，从而对敌方造成更高的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/128/12800.png');",
                "INSERT INTO `skill` VALUES ('1281', '128', '霸道之刃', '10', '0', '曹操向指定方向发起斩击突袭，对路径上的敌人造成160/185/210/235/260/285（+84%物理加成）点物理伤害；最多可以发动3次，每次斩击间隔不能超过8秒，并且第三次斩击会将敌人击飞0.5秒', '这个技能使得曹操非常灵活，连续释放三次可有以产生很远的位移和伤害.不论在追人或逃跑时都非常实用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/128/12810.png');",
                "INSERT INTO `skill` VALUES ('1282', '128', '纵横天下', '12', '0', '曹操剑指天下，向指定方向挥出强大剑气，对路径上的敌人造成240/300/360/420/480/540（+90%物理加成）点物理伤害并减少其50%移动速度，持续2秒；被动：普通攻击命中敌人时减少1秒纵横天下的冷却时间', '对直线上敌人造成物理伤害，并减速，配合被动技能可以频繁使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/128/12820.png');",
                "INSERT INTO `skill` VALUES ('1283', '128', '浴血枭雄', '30', '0', '曹操以鲜血意志强化大剑，持续8秒，施放时对附近敌人造成300/375/450（+100%物理加成）点物理伤害，强化的大剑会增加100/150/200物理攻击力和20%韧性；并且每次普通攻击或技能命中敌方将回复120/180/240（+40%物理加成）点生命值', '这个技能在提升曹操伤害的同时，也增强了曹操在团战中的生存能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/128/12830.png');",
                "INSERT INTO `skill` VALUES ('1290', '129', '激怒', '0', '0', '被动：典韦每一次击败英雄或者助攻会增加1层激怒效果，每层激怒效果会增加典韦12点物理攻击力，最多叠加20层', '在前中期，典韦需要尽量的蹭队友助攻或击杀更多敌人，可以保证优势的建立', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/129/12900.png');",
                "INSERT INTO `skill` VALUES ('1291', '129', '红眼', '9', '55', '典韦做好冲锋陷阵的准备，移除自身控制效果，并且增加60%移动速度，持续3秒，下一次普通攻击变更为挥砍，挥砍会对附近敌人造成额外250/300/350/400/450/500（+120%物理加成）点物理伤害', '较高的攻速与移速加成，可以利用此技能开团，也可灵活选择撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/129/12910.png');",
                "INSERT INTO `skill` VALUES ('1292', '129', '狂暴', '8', '65', '暴怒的典韦践踏大地，对范围内敌人造成250/280/310/340/370/400（+100%物理加成）点物理伤害并减少其50%的移动速度，持续2秒，如果命中敌人将会突破自身极限，增加200%攻击速度和50%吸血效果，持续3秒', '典韦的群体控制技能，范围减速，配合队友的控制能创造完美的输出环境', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/129/12920.png');",
                "INSERT INTO `skill` VALUES ('1293', '129', '嗜血', '30', '130', '典韦对锁定目标发起跳斩，造成300/375/450（+140%物理加成）点真实伤害并减少其50%的移动速度，持续3秒，同时会强化自身，对敌人造成的所有伤害附带目标最大生命3%的真实伤害，持续5秒。', '典韦最强的输出技能，自身附带真实伤害，不论对前后排英雄都能轻松击杀', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/129/12930.png');",
                "INSERT INTO `skill` VALUES ('1110', '111', '活力迸发', '0', '0', '被动：孙尚香每次普通攻击都会减少0.5秒翻滚突袭的冷却时间', '利用更多的普攻来减少1技能翻滚突袭的CD时间，可以让孙尚香更灵活', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/111/11100.png');",
                "INSERT INTO `skill` VALUES ('1111', '111', '翻滚突袭', '5', '35', '孙尚香向指定方向翻滚，下一次普通攻击变更为强化射击，强化射击会对一条直线上的敌人造成300/330/360/390/420/450（+100%物理加成）点物理伤害，如果翻滚后附近有敌方英雄将会增加80%持续衰减的移动速度，持续2秒，使用强化射击后会重置下一次普通攻击并增加攻击距离。', '孙尚香主要的位移技能和输出技能，用于追人和撤退使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/111/11110.png');",
                "INSERT INTO `skill` VALUES ('1112', '111', '红莲爆弹', '10', '50', '孙尚香向指定目标范围投掷爆弹，对范围内敌人造成200/240/280/320/360/400（+100%物理加成）点物理伤害并减少90%移动速度，持续1秒，命中敌方后增加自身物理穿透20%，持续4秒。', '相当于一个范围性的软控制技能，命中敌人后，能让孙尚香更好的输出敌人或选择撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/111/11120.png');",
                "INSERT INTO `skill` VALUES ('1113', '111', '究极弩炮', '30', '100', '孙尚香蓄力向指定方向发射一枚弩炮，弩炮在触碰敌人或者飞行到最远距离后将发生爆炸，对第一名触碰的敌人造成500/750/1000（+185%物理加成）点物理伤害，对爆破范围内的敌人造成50%伤害', '这个技能最好在战斗初使用，尽量将敌人打残，让孙尚香尽早进入收割的节奏', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/111/11130.png');",
                "INSERT INTO `skill` VALUES ('1570', '157', '忍蜂', '0', '0', '被动：不知火舞脱离战斗后的第一次普通攻击将会向当前朝向释放忍蜂，造成100（+100%法术加成）的法术伤害并将敌人击退；不知火舞使用技能后能够在结束时翻滚一段距离，并且会增加50%移动速度，持续0.5秒，该效果会随时间衰减。', '被动技能不但加强了不知火舞的输出伤害，并且同时提升了该英雄的机动能力，在每第3次普攻触发被动前，你可以调整自己的走位，将敌人推向你需要的位置.在每次施放技能后，你都可以立即操纵轮盘方向来控制翻滚的方向，进可攻退可守', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/157/15700.png');",
                "INSERT INTO `skill` VALUES ('1571', '157', '飞翔龙炎阵', '10', '70', '不知火舞向上飞踢腾空，对范围内敌人造成550/620/690/760/830/900（+73%法术加成）点法术伤害并将其击飞0.75秒；技能命中回复25点能量值', '飞翔龙炎阵是不知火舞的主要输出与控制手段.利用此技能位移靠近目标并形成短暂的击飞效果，在此时间内配合其他技能或普攻尽量打出被动，使得输出最大化.当逃跑时，这个技能可以当作一个不错的位移技能来使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/157/15710.png');",
                "INSERT INTO `skill` VALUES ('1572', '157', '花蝶扇', '4', '40', '不知火舞扔出扇子，对第一个命中的目标造成500/590/680/770/860/950（+100%法术加成）点法术伤害并减少其90%移动速度，持续1秒，同时减少50/100/150/200/250/300点法术防御，持续3秒；技能命中将回复25点能量值', '花蝶扇除了提供可观的法术伤害还可造成短暂的减速效果，是一个不错的留人技能.需要注意的是，花蝶扇只能对路径上的第一个命中目标有效，所以当敌人站位在兵线或坦克英雄身后时，请一定要等待时机施放花蝶扇，配合被动技能的翻滚位移，在追击和逃跑时都能轻松应对', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/157/15720.png');",
                "INSERT INTO `skill` VALUES ('1573', '157', '必杀·忍蜂', '25', '110', '不知火舞向前冲刺，对命中的敌人造成800/1000/1200（+110%法术加成）点法术伤害并将其击退；同时敌人会减少20%/25%/30%物理攻击力，持续2.5秒；技能命中将回复25点能量值', '必杀·忍蜂是不知火舞超强力的AOE输出技能，向自定方向冲刺，造成高额的法术伤害并伴随击退效果，团战时，该技能命中的敌方英雄数往往决定了一场团战的成败.所以，你需要在团战中注意走位、找准时机，尽量在此技能施放时打到更多的敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/157/15730.png');",
                "INSERT INTO `skill` VALUES ('1440', '144', '舍生忘死', '0', '0', '被动：程咬金每损失1%最大生命值获得额外3～7点物理攻击力，增加幅度随英雄等级成长，并且程咬金释放普通技能会消耗当前8%的最大生命值，大招消耗16%的当前生命值；每次释放技能还会回复6%已损失生命值，回复过程持续3秒', '被动是以损失最大生命值来增加伤害输出，但同时也提供了回复效果，所以纯肉装也是不错的选择', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/144/14400.png');",
                "INSERT INTO `skill` VALUES ('1441', '144', '爆裂双斧', '15', '0', '程咬金向指定目标位置猛力一跃并挥动双斧斩击，对范围内敌人造成120/155/190/225/260/295（+50%物理加成）点物理伤害并造成50%的减速效果，持续2秒；被动：程咬金的普通攻击命中敌方英雄会减少1秒爆裂双斧的冷却时间。', '程咬金位移的控制技能，被动效果普通攻击可减少CD，对线或是抓人时可以作先手减速留人，持续输出粘住敌人.掩护队友时，也可让队友从容撤退.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/144/14410.png');",
                "INSERT INTO `skill` VALUES ('1442', '144', '激热回旋', '5', '0', '程咬金转动双斧劈砍敌人，对范围内的敌人造成两段伤害，每段造成125/150/175/200/225/250（+60%物理加成）点物理伤害。', '程咬金的主要输出技能，用来清线是不错选择，团战中，由于CD很短可多次使用该技能进行AOE伤害输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/144/14420.png');",
                "INSERT INTO `skill` VALUES ('1443', '144', '正义潜能', '30', '0', '程咬金迸发正义热情，每秒回复8%最大生命，同时增加移动速度30%，持续5秒。', '程咬金在团战中保证了自己的超高生存能力，持续回复大量生命值并提升移动速度，在追人和撤退时使用都是很好的选择', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/144/14430.png');",
                "INSERT INTO `skill` VALUES ('1660', '166', '圣光守护', '0', '0', '被动：亚瑟获得圣光守护，每2秒回复1%的最大生命值', '保证了亚瑟充足的赖线能力，因为本身亚瑟就是一个无蓝耗英雄，加上他的恢复能力，只要他不跟对手硬拼，一般情况下很难被击杀', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/166/16600.png');",
                "INSERT INTO `skill` VALUES ('1661', '166', '誓约之盾', '10', '0', '亚瑟增加30%移动速度，持续3秒，下一次普通攻击变更为跳斩，跳斩会造成180/205/230/255/280/305（+100%物理加成）点物理伤害并将目标沉默1秒，同时跳斩命中的敌方英雄会被标记，持续5秒，亚瑟的普通攻击和技能会额外造成目标最大生命值1%的法术伤害；标记附近的友军会增加10%的移动速度', '这是亚瑟的主要技能，移速加成可以很好的用来追击或逃跑.附加的护盾效果可用于对线耗血使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/166/16610.png');",
                "INSERT INTO `skill` VALUES ('1662', '166', '回旋打击', '12', '0', '亚瑟召唤圣盾围绕自身旋转，对路径上的敌人会造成145/163/181/199/217/235（+80%物理加成）点物理伤害，圣盾最多持续5秒', '范围性伤害技能，线上用于快速清兵，再有了一定装备后，团战中可以造成非常可观的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/166/16620.png');",
                "INSERT INTO `skill` VALUES ('1663', '166', '圣剑裁决', '42', '0', '亚瑟举起圣剑跃向敌方英雄，造成敌方英雄最大生命12/16/20%法术伤害并将其击飞0.5秒；同时召唤圣印覆盖落点范围，持续5秒，圣印会对范围内敌人造成每秒85/105/125点法术伤害', '这个技能在最后收割的时候使用，敌方血量损失越多，伤害越高，千万不要在目标血量较高时使用此技能', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/166/16630.png');",
                "INSERT INTO `skill` VALUES ('1530', '153', '秘技·极意', '0', '0', '被动：兰陵王朝敌方英雄移动时会增加20%移动速度', '被动技能让兰陵王更容易黏住敌人，并且无需借助位移技能，也就不会打断普攻节奏，因此兰陵王在追击敌人时也可以保持较高的普攻频率', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/153/15300.png');",
                "INSERT INTO `skill` VALUES ('1531', '153', '秘技·分身', '5', '50', '兰陵王以拳刃挥砍身边敌人，造成215/260/305/350/395/440（+85%物理加成）点物理伤害，同时召唤出影分身随机攻击附近一名敌人，影分身会造成等量的伤害', '这是兰陵王主要的输出手段，用来对付落单的敌人更加有效，在团战时，兰陵王也可以调整自己的走位远离混战中心，让自己和猎物处于一个相对独立的范围内，更加有效的对猎物进行输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/153/15310.png');",
                "INSERT INTO `skill` VALUES ('1532', '153', '秘技·影蚀', '9', '80', '兰陵王向指定方向投射暗影匕首，造成165/186/207/228/249/270（+57%物理加成）点物理伤害并减少其90%移动速度，持续2秒；匕首命中的敌人会被标记，持续3秒，期间再次受到兰陵王攻击将晕眩1秒并使兰陵王回复140/168/196/224/252/280（+50%物理加成）点生命值；标记消失时该目标还会受到已损失生命值24%物理伤害', '匕首的2段伤害是基于已损失生命值的，因此如果能在此时间段内尽量伤害敌人，就可以大大提升伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/153/15320.png');",
                "INSERT INTO `skill` VALUES ('1533', '153', '秘技·隐袭', '36', '120', '兰陵王开始准备并于1.5秒后进入隐身状态，持续30秒，期间再次施放该技能，将向指定方向冲锋并对路径上敌人造成660/830/1000（+188%物理加成）点物理伤害，解除影身状态后兰陵王会增加50%攻击速度，持续5秒；隐身时若处于敌人附近超过3秒，隐身状态将会自动解除', '进入隐身状态，兰陵王可利用此技能神不知鬼不觉的贴近猎物将其击杀，当然这个技能也能用来逃命使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/153/15330.png');",
                "INSERT INTO `skill` VALUES ('1980', '198', '食梦', '0', '0', '被动：梦奇不断吞噬周围的噩梦，导致长胖；从最瘦到最胖总共会增长100点质量，耗时20秒；长胖会增加普通攻击和梦境萦绕、梦境漩涡攻击范围，并且最多可以增加150点物理攻击、200点物理和法术防御、92自然回血值；同时最多减少240点移动速度；梦奇使用技能会消耗质量，导致减肥；梦奇的第三次普通攻击将变更为横扫，横扫会造成更大范围的伤害', '合理调整自身体积，在赶路和与敌人周旋时适当缩小体积提高移速，但要确保靠近敌人后拥有足够大的体积进行战斗。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/198/19800.png');",
                "INSERT INTO `skill` VALUES ('1981', '198', '梦境萦绕', '3', '40', '梦奇消耗质量，形成可抵免525/630/735/840/945/1050（+120%法术加成）点伤害的护盾，护盾生成瞬间对周围敌人造成350/420/490/560/630/700（+80%法术加成）法术伤害并减少其50%移动速度，持续1.5秒。使用技能后7秒内下三次普通攻击命中敌人，每次均可回复6点质量', '主要生存技能，也是调控质量的手段之一，带有减速效果。注意技能伤害范围会随质量提升。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/198/19810.png');",
                "INSERT INTO `skill` VALUES ('1982', '198', '梦境挥洒', '5', '40', '梦奇将噩梦盛装在容器中抛出，容器落地破碎造成200/250/300/350/400/450（+45%法术加成）点法术伤害，噩梦会残留在地面，最多持续5秒，每0.5秒对范围内的敌人造成100/125/150/175/200/225（+22%法术加成）点法术伤害并且范围内敌人将减少30%移动速度。梦奇若触碰残留在地面的噩梦，便会将其回收；同时最多可残留2团噩梦', '释放时需要一定的预判，考虑施法位置时，除了要尽量直接命中敌人，也要能让自己去触碰回收消耗的质量。该技能同样是常用的调控质量手段之一。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/198/19820.png');",
                "INSERT INTO `skill` VALUES ('1983', '198', '梦境漩涡', '40', '0', '梦奇引导噩梦向指定位置输送，形成噩梦漩涡，对范围内的敌人每0.5秒造成150/225/300（+20%法术加成）点法术伤害并会减少50%移动速度，引导最多持续3秒；再次释放或输送结束，梦奇会传送到指定位置并拾起噩梦，对范围内敌人造成300/450/600（+40%法术加成）点法术伤害并将其击飞1秒；引导可被打断，并会执行50%冷却时间；梦奇至少拥有33点质量才能引导噩梦输送。被动：梦奇释放出的噩梦造成伤害时，目标会增加1层噩梦缠绕，每层噩梦缠绕会将其减少8%法术攻击力，持续4秒，最多可叠加5层', '如果希望传送到目的地，那么尽量完整吟唱整个技能，这样传送后拥有较高质量。但也可以选择在吟唱期间取消技能，这样可只作为一个远程软控技能使用。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/198/19830.png');",
                "INSERT INTO `skill` VALUES ('5040', '504', '机械仆从', '0', '0', '被动：直接被米莱狄或者机械仆从击杀的单位（除敌方机械仆从以外），会在其附近召唤1个机械仆从（至多存活8秒），短暂时间后冲向附近的敌人进行攻击。（机械仆从的攻击力,生命值和移动速度受到米莱狄的法术强度加成，机械仆从至多存在5个，超过上限时自动引爆更早的机械仆从）。机械仆从如果没有被击杀，生存期结束后将会发生爆炸对附近敌人造成200（+30%法术加成）点法术伤害。机械仆从攻击力：150（+30%法术加成）,机械仆从生命值：500（+100%法术加成）,机械仆从移动速度：450（+10%法术加成）', '机械仆从是米莱狄最大的依仗，在对线或消耗战斗中，米莱狄不断击杀从而召唤的机械仆从可以轻松滚起胜利的雪球', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/504/50400.png');",
                "INSERT INTO `skill` VALUES ('5041', '504', '空中力量', '8', '60', '米莱狄指挥飞行器朝指定方向飞出，对沿路的敌人进行扫射造成60/70/80/90/100/110（+6%法术加成）点法术伤害。飞行器连续攻击2次以后，下一次攻击将会对目标发射一枚导弹，造成120（+12%法术加成）点法术伤害。飞行器在飞行的过程中，米莱狄可以发出指令让飞行器进行一次分裂，变为2个飞行器进行攻击，分裂的飞行器造成45/53/61/69/77/85（+4%法术加成）点法术伤害如果敌人同时受到2个飞行器的攻击，将会被减速50%持续1.5秒。', '米莱狄的主要消耗技能，合理的预判飞行路径和在合适的时机分裂飞机可以造成大量伤害的同时限制对手的移动能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/504/50410.png');",
                "INSERT INTO `skill` VALUES ('5042', '504', '强制入侵', '3', '25', '米莱狄标记一个位置，短暂时间后召唤两个机械仆从，落地时对附近敌人造成100/120/140/160/180/200（+20%法术加成）点法术伤害，并在1秒后激活，自动攻击附近的敌方单位，机械仆从存活至多8秒。每20/19/18/17/16/15秒储备一个机械仆从，至多储备3个', '米莱狄的核心技能，在机械仆从数量不足时，米莱狄可以主动的增加场面上仆从的数量以支持其战斗', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/504/50420.png');",
                "INSERT INTO `skill` VALUES ('5043', '504', '浩劫磁场', '35', '100', '米莱狄朝指定目标发出一颗信号装置，标记并晕眩敌人持续0.75秒，被标记目标受到磁场干扰每0.5秒造成100/150/200（+5%法术加成）点法术伤害，并减少30%移动速度持续3秒，干扰效果结束时引发磁场爆炸攻击附近的敌人，造成基于干扰期间来自米莱狄和她的机械部队对其造成的总伤害30%/40%/50%法术伤害。如果磁场爆炸直接击败了被标记的英雄或者在标记期间死亡，将在其附近召唤3个机械仆从。当机械仆从发现标记的目标时，将会立即冲向该目标发起攻击。', '机械仆从会在大招标记后集火攻击标记目标，在有足够数量的机械仆从在行动时，大招的第二段伤害会有可观的提升', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/504/50430.png');",
                "INSERT INTO `skill` VALUES ('5030', '503', '无畏战车', '0', '0', '被动：狂铁的武器在击中敌人时会激发电流，进而为武器充能。普攻每命中一个英雄/非英雄回复7点/5点能量,技能每命中一个英雄/非英雄回复9点/6点能量，最多可储存90点能量。充能＞30点时，技能会获得不同的强化效果。强化技能使用时会消耗30点能量，且不会因击中敌人而获取能量。脱离战斗后，能量每秒减少3点', '狂铁在高能量状态下，拥有更强的技能效果，合理的安排技能释放，能够最大化的增幅狂铁的战斗能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/503/50300.png');",
                "INSERT INTO `skill` VALUES ('5031', '503', '碎裂之刃', '7', '0', '狂铁奋力向前横挥武器两次，每次造成150/170/190/210/230/250(+100%物理加成)点物理伤害。每命中一个敌人回复自身已损生命值2%+40/48/56/64/72/80（+20%物理加成）点生命值。充能强化：伤害提升至225/255/285/315/345/475（+150%物理加成）点物理伤害，生命回复提升至已损生命值4%+80/96/112/128/144/160（+40%物理加成）点生命值。作用于非英雄单位时，生命回复效果减半。', '已损生命值比例的回复效果，让狂铁在后手攻击也毫不吃亏，当在兵线附近时，狂铁的回复能力还会更加强大', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/503/50310.png');",
                "INSERT INTO `skill` VALUES ('5032', '503', '强袭风暴', '10', '0', '狂铁向前冲锋，冲锋路径上的敌人受到150/180/210/240/270/300(+75%物理加成)点物理伤害。冲锋结束后，狂铁的下次普攻得到强化，造成200/240/280/320/360/400（+100%物理加成）点物理伤害和50%减速效果持续2秒。充能强化：在释放强化普攻时若能量＞30，强化普攻伤害提升至300/360/420/480/540/600（+183%物理加成）点物理伤害，对受击者造成1秒击飞和晕眩，以及之后1秒50%减速。且在释放第一次强化普攻时消耗30点能量', '冲锋并不会消耗能量，利用冲锋后的突袭效果，将能量强化在其他技能上也可能会发挥更大的作用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/503/50320.png');",
                "INSERT INTO `skill` VALUES ('5033', '503', '力场压制', '30', '0', '狂铁将武器重重砸向地面，使电流向周围蔓延，电流对触及的敌人造成250/375/500（+100%物理加成）点物理伤害和1.5秒50%减速。强烈的电流还会形成可抵挡1000/1500/2000点伤害的护盾，护盾每0.5秒对周围敌人造成50/75/100（+20%物理加成）点物理伤害，并且每次伤害为狂铁汲取4点能量（从非英雄单位汲取2点）,护盾最多持续6秒。充能强化：伤害提升375/563/751（+150%物理加成）点物理伤害，护盾值提升至1500/2250/3000。', '强化的护盾持续期间，可以为狂铁带来大量的能量值，进而强化其余技能，增加其爆发能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/503/50330.png');",
                "INSERT INTO `skill` VALUES ('1250', '125', '秘术·操控', '0', '0', '被动：傀儡将继承元歌全部装备（除复活甲）效果以及部分BUFF效果，傀儡普通攻击附带0~195（+100%物理加成）点物理伤害(基础伤害随1技能等级成长)，获得的金币经验与部分BUFF效果都会共享给元歌，傀儡被杀死会暴露元歌的视野，禁止傀儡使用，同时对元歌造成晕眩；每当傀儡回归本体，本体都会清除控制效果并获得40%加速持续1秒和200（+62%物理加成）点护盾效果', '适时的收回傀儡避免傀儡死亡让本体陷入危机', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/125/12500.png');",
                "INSERT INTO `skill` VALUES ('1251', '125', '秘术·影', '12', '100', '元歌将傀儡向指定方向掷出，对路径上敌人造成300/360/420/480（+125%额外物理加成）点物理伤害和击飞效果，并开始操控傀儡，傀儡会模仿一个敌方英雄直到攻击或者受到攻击；傀儡位于元歌900范围内时元歌会进行自动攻击；傀儡超过元歌2500范围5秒后会自动销毁；傀儡一旦死亡或销毁控制权将立即转回元歌本体身上', '释放傀儡后，地面会出现范围标识', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/125/12510.png');",
                "INSERT INTO `skill` VALUES ('1252', '125', '秘术·纸雏鸾', '9', '60', '元歌在短暂延迟后向指定方向掷出4枚暗器，对路径上敌人造成450/550/650/750（+220%额外物理加成）物理伤害，暗器可以连续命中多枚，但伤害将折减70%', '虽然会折减伤害，但是靠近敌人还是可以打出巨额的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/125/12520.png');",
                "INSERT INTO `skill` VALUES ('1253', '125', '秘术·十字闪', '9', '60', '元歌甩出丝线向后拉扯，每根丝线对命中目标造成250/295/340/385（+100%额外物理加成）点物理伤害，被两条丝线命中的单位将会受到额外已损生命16%的物理伤害和50%减速效果持续2秒', '元歌拉扯丝线时，会朝后方移动一段距离', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/125/12530.png');",
                "INSERT INTO `skill` VALUES ('1254', '125', '秘术·散', '12', '100', '元歌立即消失清除控制效果，并在短暂延迟后出现在范围内的另一位置', '元歌短暂消失时无法被攻击，利用该技能位移时，还能躲避关键技能', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/125/12540.png');",
                "INSERT INTO `skill` VALUES ('1330', '133', '迅捷', '0', '0', '被动：狄仁杰的普通攻击能够获得1层迅捷效果，每层迅捷效果会增加3％攻击速度和5％移动速度，最多叠加5层', '这个技能能让狄仁杰的持续输出能力得到显著额提升', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/133/13300.png');",
                "INSERT INTO `skill` VALUES ('1331', '133', '六令追凶', '8', '60', '狄仁杰向指定方向甩出六道令牌，对命中的目标造成65％物理加成点物理伤害和80/88/96/104/112/120（+20％法术加成）（+40％额外物理加成）点法术伤害，其中蓝牌只能造成50％的伤害，但蓝牌会减少目标90％移动速度，持续0.5秒，当同一目标受到多道令牌伤害，从第二道令牌开始伤害会衰减至初始伤害的70％，令牌可以触发普通攻击的法球效果；被动：狄仁杰持续使用普通攻击时，每两次普通攻击后下一次普通攻击将会随机强化为一张蓝牌或红牌', '这个技能是狄仁杰主要输出技能，有很强的推线能力，攻击敌人时能造成很好的AOE伤害效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/133/13310.png');",
                "INSERT INTO `skill` VALUES ('1332', '133', '逃脱', '14', '70', '狄仁杰向周围掷出八道令牌，清除自身负面效果，增加25％移动速度，持续1秒，令牌的伤害和效果与六令追凶相同', '这个技能能提升狄仁杰的自保能力，被控制后开启得到移速提升，可以在追人或逃跑时使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/133/13320.png');",
                "INSERT INTO `skill` VALUES ('1333', '133', '王朝密令', '30', '100', '狄仁杰向指定方向掷出一道金色密令，对命中的第一个敌方英雄造成300/375/450(+60％法术加成）（+120％额外物理加成）点物理伤害和300/375/450(60％法术加成）（+120％额外物理加成）点法术伤害，并将其晕眩1秒，同时会减少目标30％物理防御和法术防御，命中后会获得目标视野，持续5秒；期间友军向该目标移动时会增加10％移动速度；', '这个技能是狄仁杰主要的控制技能，在攻击前排时使用，降低其护甲魔抗，打前排能力大大增强', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/133/13330.png');",
                "INSERT INTO `skill` VALUES ('1770', '177', '追猎', '4', '0', '被动：当成吉思汗穿越草丛时会增加40%移动速度，持续2秒，同时下一次普通攻击将变更为两连射，每支箭矢造成0（+65%物理加成）点物理伤害；两连射有4秒冷却时间', '这个技能使得成吉思汗在野区作战能力与机动性十足，利用草丛移速优势能更好的追击敌人或快速撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/177/17700.png');",
                "INSERT INTO `skill` VALUES ('1771', '177', '鹰眼', '16', '80', '成吉思汗释放猎鹰侦查，照亮周围区域并获取其中敌人的视野，持续5秒；猎鹰还会朝指定方向飞行，获取沿途区域视野；每16秒可准备1只猎鹰，同时最多可拥有2只猎鹰；被动：箭矢对同一个目标的第三次攻击会额外造成80/160/240/320/400/480（+40%物理加成）点物理伤害', '在对局中，1技能可以用来照亮附近的视野，对于防GANK有非常好的效果，在攻击敌人时，尽量打出三发箭矢在同一名敌人身上，以造成更高的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/177/17710.png');",
                "INSERT INTO `skill` VALUES ('1772', '177', '百兽陷阱', '10', '60', '成吉思汗向指定位置投掷陷阱，持续120秒，陷阱被敌人触碰后范围内敌人将减少30%移动速度并将暴露视野；短暂延迟后对区域内造成360/432/504/576/648/720（+120%物理加成）点物理伤害；每12秒可准备一枚陷阱，同时最多储备2个陷阱；未被触发的陷阱落地2秒后隐形', '成吉思汗可以放置陷阱在关键的通道上，限制敌人走位，敌人触发陷阱后会减速并暴露视野.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/177/17720.png');",
                "INSERT INTO `skill` VALUES ('1773', '177', '可汗狂猎', '5', '30', '成吉思汗骑行中对敌人射出箭矢造成160/240/320（+100%物理加成）点物理伤害，箭矢可触发普通攻击的法球效果并可以暴击。每5秒可准备1只箭矢，同时最多可储备5只箭矢。', '成吉思汗可以累积多支箭矢，箭矢CD非常短可以进行持续输出，配合普通攻击施放效果更佳', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/177/17730.png');",
                "INSERT INTO `skill` VALUES ('1940', '194', '不屈铁壁', '120', '0', '被动：苏烈在濒临死亡时会立即回复60%最大生命值，同时会陷入虚弱状态，持续4.5秒；虚弱状态下将禁止一切操作并免疫控制效果，自身会减少25%物理和法术防御；虚弱状态结束时，苏烈将发出怒吼，对附近敌人造成150（+100%物理加成）法术伤害并减少其50%移动速度，持续2秒；虚弱状态结束时，苏烈还将增加50%攻击速度和30%移动速度，持续2秒；刚陷入虚弱状态时，苏烈周围会出现3盏烽火台，队友可通过触碰烽火台来点亮烽火，每点亮一盏将会为苏烈和该名队友回复10%最大生命值;3盏烽火台都被点亮时，会立即结束虚弱状态；苏烈每120秒才能触发一次虚弱状态；苏烈的意志力能增加60点物理防御和法术防御，但不屈铁壁处于冷却时会暂时失效', '复活过程中，需要队友协助点燃烽火，让苏烈可以快速站起投入战斗。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/194/19400.png');",
                "INSERT INTO `skill` VALUES ('1941', '194', '烽烟踏破', '5', '60', '苏烈的下三次普通攻击变更为强化攻击，每次攻击造成100/130/160/190/220/250（+100%物理加成）点物理伤害，第三次攻击会将范围内的敌人击飞0.75秒。（攻击完成或者强化效果结束时进入冷却）', '主要输出技能，第三击可以击飞敌人，配合被动的加速效果时，可以在短时间内打出高额伤害。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/194/19410.png');",
                "INSERT INTO `skill` VALUES ('1942', '194', '所向披靡', '10', '80', '苏烈短暂蓄力后，向指定方向冲撞，对敌人造成100/130/160/190/220/250（+40%物理加成）点物理伤害并将其击退。如果将敌人击退至地形边缘，将额外造成50%伤害并将其晕眩1秒。如果苏烈冲撞敌方建筑，将对建筑造成250（+100%物理加成）点物理伤害。', '释放时需要一定的预判，尽量将对手冲撞到地形边缘造成晕眩效果。推进的时候，可以释放此技能直接攻击防御塔。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/194/19420.png');",
                "INSERT INTO `skill` VALUES ('1943', '194', '豪烈万军', '45', '120', '苏烈举起柱子开始蓄力，释放时会用力将柱子砸向地面，对范围内敌人造成300/375/450（+150%物理加成）点~1000(+150%物理加成)点物理伤害并将其击飞1秒；蓄力时间越长，攻击范围越大，伤害越高；被击飞的敌人在落地时还会以该名敌人为中心再产生一次震荡攻击，对附近的其他敌人额外造成50%物理伤害；蓄力期间可以移动，取消时会减少50%冷却时间；被动：苏烈释放技能攻击或者强化攻击时将引发地震，持续3秒，对附近的敌方目标造成38（+25%物理加成）点法术伤害', '蓄力阶段要避免被对手打断，时刻观察目前的攻击范围和敌人的分布情况，确保在最佳的时机进行释放，对敌人造成毁灭一击。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/194/19430.png');",
                "INSERT INTO `skill` VALUES ('1730', '173', '密探谛听', '0', '0', '被动：元芳可爱的大耳朵可以帮助元芳周期性谛听周围的动向，获得并捕捉敌方英雄的踪迹，获得敌方视野1秒，对同一单位6秒生效一次。', '李元芳因为被动的存在，使得他能快速清理兵线和野怪，射程的优势也能保证他一定的安全性', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/173/17300.png');",
                "INSERT INTO `skill` VALUES ('1731', '173', '谍影重重', '10', '60', '元芳使用暗印标记敌方目标，最多持续4秒，4秒后爆炸对范围内敌方造成100/120/140/160/180/200(+65%物理加成）点物理伤害，元芳对该目标的普通攻击会叠加暗印标记，最多叠加4层，每层印记会额外造成100/120/140/160/180/200(+65%物理加成）点物理伤害，叠加满4层会立即引爆暗印；释放技能时元芳会增加40%攻击速度和75点攻击距离，持续4秒', '暗印一旦挂上，延时爆炸的威胁使得敌人会非常紧张.团战时，暗印的爆炸范围伤害很容易伤到附近的敌人，造成不错的AOE伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/173/17310.png');",
                "INSERT INTO `skill` VALUES ('1732', '173', '刃遁', '12', '80', '元芳向指定方向放出飞轮，同时施展鬼魅身法在终点现身接住飞轮，移动时元芳将无法被选中，飞轮会对敌人造成80/90/100/110/120/130（+67%物理加成）点物理伤害并减少50%移动速度，持续2秒；经过的地面留下燃痕，对路径上的敌人每0.25秒造成30/36/42/48/54/60（+25%物理加成）点物理伤害并减少50%移动速度', '元芳进攻时，当刃盾的飞轮冲撞到敌人时，会对敌人造成减速，这时候一旦挂上暗印，敌人很难逃跑.遇到GANK时，飞轮作为位移逃走，飞轮留下的燃痕可以让人无法从最近的路来追赶元芳', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/173/17320.png');",
                "INSERT INTO `skill` VALUES ('1733', '173', '无间刃风', '36', '130', '元芳平扔出飞轮到指定位置旋转卷起利刃风暴，利刃撞击目标中造成215/275/335（+75%物理加成）点物理伤害，持续5.4秒，对范围内的敌人每0.5秒造成75/93/111（+30%物理加成）点物理伤害并减少50%移动速度，内圈会受到双倍的刃伤；刃伤也可叠加暗印层数。', '这个技能除了是一个持续的大范围伤害技能外，更是一个战略性的封路技能，你完全可以把它当作你输出的堡垒，肆意的在刃风的区域走位输出，而敌人进入这个区域会深陷泥潭，灵活的利用这个技能是玩好李元芳的秘诀', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/173/17330.png');",
                "INSERT INTO `skill` VALUES ('1890', '189', '纵横兵法', '0', '0', '被动：鬼谷子脱离敌方视野3秒后，玄微子会再次归巢到法杖中，下一次普通攻击变更为强化攻击，强化攻击会造成300（+45%法术加成）点法术伤害并将范围内的敌人减少50%移动速度，持续1.5秒；如果使用强化攻击时，鬼谷子不处于敌方视野，则纵横兵法的冷却时间将会增加至10秒', '这个技能能帮助鬼谷子去游走时能很好的减速留下敌人，需要配合队友输出将敌人击杀', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/189/18900.png');",
                "INSERT INTO `skill` VALUES ('1891', '189', '先知·神隐', '10', '70', '鬼谷子命令玄微子包裹自身，使自己与周围环境融为一体进入伪装状态，持续4秒；伪装状态下，鬼谷子将增加40%移动速度，并且对触碰到的每个敌人造成450/540/630/720/810/900（+60%法术加成）点法术伤害，同时降低该敌人25%的物理和法术防御，持续4秒，每个敌人最多触发1次；伪装效果会在鬼谷子用其它方式伤害敌人时消失', '这个技能降低敌人护甲和魔抗，能很好的提升我方输出英雄打前排的能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/189/18910.png');",
                "INSERT INTO `skill` VALUES ('1892', '189', '万物有灵', '10', '80', '鬼谷子召唤大量玄微子在自身周围聚集，对范围内敌人每0.5秒造成100/120/140/160/180/200（+15%法术加成）点法术伤害，持续2秒；2秒后玄微子集结完毕，对范围内敌人造成400（+60%法术加成）点法术伤害，同时将敌人拉扯到鬼谷子身旁并将其晕眩1秒；如果该伤害命中敌方英雄，鬼谷子和范围内的队友将获得可抵免500/600/700/800/900/1000（+100%法术加成）点伤害的护盾', '这个技能是鬼谷子作为辅助的核心控制技能，需要把握技能施放时机，和合理的走位，才能使得它能控制更多的敌人。同时，给附近的队友加护盾，让队友抵消一些伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/189/18920.png');",
                "INSERT INTO `skill` VALUES ('1893', '189', '先知·雾隐', '35', '120', '鬼谷子命令玄微子包裹自身和1500距离内的队友，准备1秒后进入伪装状态；伪装状态下鬼谷子和队友将增加30%移动速度；同时将会探查到距离鬼谷子最近的一名敌方英雄视野，伪装状态最多持续4秒；伪装状态下，如果使用普通攻击、技能或敌方造成负面效果会立即解除伪装状态', '这个技能是鬼谷子的核心功能，开启大招，附近友军群体隐身，能够在战术上得到很好的施展，可以群体隐身抱团进攻突袭，亦可用于保护友军进行撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/189/18930.png');",
                "INSERT INTO `skill` VALUES ('1930', '193', '修罗之魂', '0', '0', '被动：铠拥有精湛的战斗技巧，当铠的普通攻击和极刃风暴只命中了一个目标时将会额外造成50%伤害。', '在铠利用2技能攻击敌方英雄时候尽可能多的利用普攻来造成额外伤害。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/193/19300.png');",
                "INSERT INTO `skill` VALUES ('1931', '193', '回旋之刃', '10', '60', '铠向指定方向投掷刀刃，对命中的敌人造成150/180/210/240/270/300（+60%物理加成）点物理伤害并减少其30%移动速度，持续1秒；刀刃命中敌人后会在敌人间弹射，最多4次；命中的第一个目标将会额外减少20%移动速度，持续1秒；当刀刃命中敌人时，铠将回复150/180/210/240/270/300（+60%物理加成）点生命值并且减少30%回旋之刃的冷却时间，同时增加20%移动速度，持续3秒', '这个技能需要预判对手走位释放，加速效果使得他在追人时能保证他粘住敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/193/19310.png');",
                "INSERT INTO `skill` VALUES ('1932', '193', '极刃风暴', '6', '50', '铠向指定方向发动两次挥砍，每次80/96/112/128/144/160（+30%物理加成）点物理伤害，并且第二次挥砍会将敌人击飞0.5秒；同时使得下一次普通攻击更变为冲砍，冲砍会冲锋至目标身旁并发动普通攻击，冲砍会额外造成80（+30%物理加成）点成物理。被动：脱离战斗后铠每秒回复1%最大生命值及最大法力值并且会增加10/12/14/16/18/20点移动速度', '这是铠的突进和主要控制技能，命中将敌人轻微击退，被动带来的移速提升能够让他更好的游走支援', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/193/19320.png');",
                "INSERT INTO `skill` VALUES ('1933', '193', '不灭魔躯', '60', '120', '铠召唤魔铠，魔铠在1秒后降临，对附近造成300/400/500点法术伤害并强化自身100/150/200攻击力、50点移动速度、60/95/130点伤害格挡；同时会对自身附近的敌人造成60点法术伤害，持续8秒；', '铠的大招在短暂延迟后才能造成伤害，需要注意走位和大招造成伤害的时机配合。同时，开启大招铠的各项属性在一段时间内都将得到极大的增强', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/193/19330.png');",
                "INSERT INTO `skill` VALUES ('1340', '134', '真言·心经', '0', '0', '被动：达摩每次释放技能将会获得真言护体，每层真言增加80点物理防御，最多叠加3层；如果技能命中目标，下两次普通攻击将增加100%攻击速度，并会造成额外的（+30%物理加成）的物理伤害并回复60点生命值', '被动技能使得达摩抗性增强，可以承受更多的伤害，技能结合普攻可以造成更高的输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/134/13400.png');",
                "INSERT INTO `skill` VALUES ('1341', '134', '真言·无相', '7', '45', '达摩往前冲刺挥出强力冲拳，对路径上的敌人造成200/240/280/320/360/400（+100%物理加成）点物理伤害并击飞目标0.5秒', '这是达摩重要的位移技能，可以用来追击和逃跑，并且能造成控制效果，留住敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/134/13410.png');",
                "INSERT INTO `skill` VALUES ('1342', '134', '真言·明王', '10', '60', '达摩快速朝指定方向连续打出五拳，每一拳造成75/90/105/120/135/150（+33%物理加成）点物理伤害并减少目标20/28/36/44/52/60点物理防御，最多叠加5层，持续5秒', '这是达摩的主要输出技能，在控制敌人后使用更能将伤害打足', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/134/13420.png');",
                "INSERT INTO `skill` VALUES ('1343', '134', '真言·普渡', '35', '100', '达摩向指定方向挥出超强一拳，对附近的敌人造成150/225/300（+65%物理加成）点物理伤害并将敌人向该方向击退；被击退的敌人如果在飞行过程中触碰了地形边缘将会再次受到150/225/300（+65%物理加成）点物理伤害并晕眩1.5秒；成功将敌方击退至地形边缘可再次释放真言·普渡，再次释放时达摩会向指定方向飞踢，对路径上的敌人造成150/225/300（+65%物理加成）点物理伤害并附带目标已损生命值16%的额外伤害', '尽量将敌方击飞到地形边缘，可以眩晕敌人并解锁大招2段释放，造成斩杀伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/134/13430.png');",
                "INSERT INTO `skill` VALUES ('1170', '117', '石之炼金', '0', '0', '被动：钟无艳造成伤害时，会有50%几率将敌方英雄石化1秒，每个敌方英雄在8秒内只会受到一次石化效果', '被动的石化效果无疑增强了钟无艳的控制效果，也变相的增加了他的生存能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/117/11700.png');",
                "INSERT INTO `skill` VALUES ('1171', '117', '狂飙突进', '5', '50', '钟无艳向指定方向突进，并将下一次普通攻击替换为重击，重击将造成225/270/315/360/405/450（+120%物理加成）点物理伤害，并且会减少50%移动速度，持续2秒', '钟无艳在追击敌人或逃跑时都能利用此技能进行位移，重击也有可观的输出伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/117/11710.png');",
                "INSERT INTO `skill` VALUES ('1172', '117', '震慑打击', '10', '70', '钟无艳蓄力后用大锤猛击地面，对范围内的敌人造成350/420/490/560/630/700（+150%物理加成）点物理伤害，对范围中心的敌人造成双倍伤害', '非常强大的范围控制技能，将范围内的敌人击飞，对敌人造成高额的伤害，配合被动的石化效果能形成非常强力的控制', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/117/11720.png');",
                "INSERT INTO `skill` VALUES ('1173', '117', '飓风之锤', '40', '100', '钟无艳抡起大锤持续旋转攻击附近敌人，持续3.2秒，每0.4秒造成150/200/250（+80%物理加成）点物理伤害，并且当敌人处于外圈时将额外造成40%伤害；被动：钟无艳召唤大地之力，每3秒生成一个持续存在的岩土护盾，岩土护盾可抵免80/120/160(+35%物理加成)点伤害', '钟无艳的主要输出技能，持续性的AOE伤害输出配合被动的石化效果会非常恐怖', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/117/11730.png');",
                "INSERT INTO `skill` VALUES ('1360', '136', '天命之女', '0', '0', '被动：武则天脱离战斗后会增加50点移动速度，同时每秒会回复1％最大法力值', '施放任意两个技能可以强化下一次1技能，并刷新冷却时间，需要注意武则天技能的施放连招', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/136/13600.png');",
                "INSERT INTO `skill` VALUES ('1361', '136', '女帝辉光', '3', '60', '武则天向指定方向释放能量法球，命中敌人后爆开，对范围内的敌人造成350/400/450/500/550/600（+45％法术加成）点法术伤害；由被动强化的女帝辉光将造成双倍伤害并将目标击飞0.75秒；被动：武则天释放任意两个技能后，女帝辉光会立即冷却并得到强化，持续3秒', '这个技能命中敌人后能减速敌人，CD比较短，配合被动技能可在短时间打出多次技能', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/136/13610.png');",
                "INSERT INTO `skill` VALUES ('1362', '136', '女帝威严', '8', '90', '武则天的威严不可侵犯，击退附近的敌人并对他们造成250/280/310/340/370/400（+30％法术加成）点法术伤害；如果技能命中敌人，自身会增加30/34/38/42/46/50％移动速度，持续1秒，命中的敌人会减少30/34/38/42/46/50％的移动速度，持续2秒', '逃命技能，面对敌人突进时，将敌人推开.这个技能也可以在队友被追时推开敌人救下队友', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/136/13620.png');",
                "INSERT INTO `skill` VALUES ('1363', '136', '生杀予夺', '80', '150', '武则天以女帝权威施行制裁，在所有敌方英雄脚下召唤法阵并引爆，对范围内敌人造成750/925/1100（+75％法术加成）点法术伤害并晕眩1秒，同时获取敌方视野，持续3秒', '这个技能在前期多用于，帮助队友控制抓人时释放，后期主要在团战中敌方站位靠近时施放能打出更多的伤害和控制', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/136/13630.png');",
                "INSERT INTO `skill` VALUES ('1160', '116', '死吻', '0', '0', '被动：阿轲在敌人身后发起的攻击，必定暴击，在敌人正面发起的所有攻击，必定不暴击；阿轲的初始暴击伤害为125%，每1%暴击几率将额外的增加0.5%暴击伤害；阿轲对非英雄目标最多造成1500伤害', '被动技能导致阿轲在敌人正面和背后的输出完全不在一个档次上，在敌人背后进行输出能造成巨大的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/116/11600.png');",
                "INSERT INTO `skill` VALUES ('1161', '116', '弧光', '4', '0', '阿轲立即向指定方向挥动双刺发动2次攻击，每次造成175/200/225/250/275/300（+65%额外物理加成）点物理伤害', '普攻间隙使用，瞬间造成大量输出，注意施法时继续移动缠住对手', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/116/11610.png');",
                "INSERT INTO `skill` VALUES ('1162', '116', '瞬华', '9', '0', '阿轲朝指定方向快速位移，在抵达终点后，立即对附近的一名敌人发起攻击，造成350/390/430/470/510/550（+100%额外物理加成）点物理伤害并标记目标，持续10秒；阿轲每次攻击标记目标时会将其减少50%移动速度，持续1秒，并缩短瞬华1秒冷却时间；阿轲被标记目标攻击时会减少30%所受到的伤害；阿轲同一时间只能标记一个敌人。', '2技能先手标记敌人后再进行输出，并且2技能的位移能帮助阿轲瞬间到达敌方背后', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/116/11620.png');",
                "INSERT INTO `skill` VALUES ('1163', '116', '幻舞', '20', '0', '阿轲短暂准备后进入隐身状态，处于隐身状态时，每0.5秒回复100/150/200（+25%额外物理加成）点生命值并增加40%移动速度，持续3秒；当阿轲发起攻击时会解除隐身状态，并在3秒内增加自身150/175/200点攻击力，同时每当对敌人造成伤害时会增加30%移动速度，持续1秒。被动：击败英雄或者助攻时刷新所有技能冷却时间。', '利用大招的隐身和加速效果切入战场并利用2技能迅速靠近敌方后排进行输出，击杀/助攻后再利用大招逃生或伺机而动', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/116/11630.png');",
                "INSERT INTO `skill` VALUES ('1260', '126', '噬目', '60', '0', '被动：夏侯惇生命值低于50％时，每一次技能或普通攻击命中目标都会回复3％的最大生命值，持续8秒；每30秒最多触发一次', '在夏侯惇生命值低于50%时，面对追击时不要一味的逃跑，可以适当的施放技能或普攻进行回血，在追击敌人时更加要利用这个回血效果，将技能的收益达到最大化', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/126/12600.png');",
                "INSERT INTO `skill` VALUES ('1261', '126', '豪气斩', '7', '50', '夏侯惇挥动大刀，向指定方向斩一道刃风，对路径上的敌人造成240/290/340/390/440/490（+100％物理加成）点物理伤害并减少其50％移动速度，持续2秒；刃风命中英雄后可再次挥动大刀向指定范围方向释放豪气斩，豪气斩会造成240/290/340/390/440/490（+100％物理加成）点物理伤害并将范围内敌人击飞1秒', '这个技能只有在第一段命中敌方英雄后才能接上第二段豪气斩击飞敌人，形成范围控制效果，第一段如果对小兵或者野怪施放时，是无法触发第二段技能的哦！', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/126/12610.png');",
                "INSERT INTO `skill` VALUES ('1262', '126', '龙卷闪', '12', '60', '夏侯惇对周围敌人造成200/225/250/275/300/325（+70％物理加成）点法术伤害；同时获得一个可抵免等同于自身额外最大生命值15％的护盾，持续5秒；并且夏侯惇的下三次普通攻击将附加100点真实伤害，持续8秒；每次强化普攻命中敌人时减少1秒不羁之刃的冷却时间', '由于是生命值加成，所以夏侯惇需要一定的血量装备，在护盾开启短时间内对敌人进行的普攻输出为真实伤害，不仅如此，每次普攻可以减少大招的冷却时间，所以可以通过此技巧对小兵或野怪进行普攻，尽快刷新大招.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/126/12620.png');",
                "INSERT INTO `skill` VALUES ('1263', '126', '不羁之刃', '15', '80', '夏侯惇向指定目标方向扔出链刃，链刃飞向指定位置或击中敌人后会对范围内敌人造成300/425/550(+118％物理加成）点物理伤害并将自己拉向链刃位置，并且击中的首个敌人会眩晕1秒；夏侯惇飞抵目标区域后还将追加300/425/550（+118％物理加成）点物理伤害', '这个技能是夏侯惇超强的留人技能，需要预判对手走位，利用大招进行追人或gank都能发挥非常好的效果，并且逃命时也可以通过对小兵、野怪甚至场景建筑施放进行位移撤退.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/126/12630.png');",
                "INSERT INTO `skill` VALUES ('1300', '130', '狩魔', '0', '0', '被动：宫本武藏施放技能后，下一次普通攻击变更为蓄力攻击，蓄力攻击会额外造成30（+60%物理加成）点物理伤害并减少1秒所有技能的冷却时间', '被动使得宫本对技能和普攻的衔接使用更起作用了，能提升输出伤害并减少技能CD', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/130/13000.png');",
                "INSERT INTO `skill` VALUES ('1301', '130', '空明斩', '10', '0', '宫本武藏向指定方向斩出一道剑气，对路径上的敌人造成340/440/540/640/740/840（+125%物理加成）点物理伤害并减少其50%移动速度，持续2秒；剑气会击落路径上的敌方飞行物；施放空明斩时宫本武藏处于霸体状态；被动：宫本武藏对敌方英雄造成伤害后会回复30/48/66/84/102/120（+21%物理加成）点生命值，回复将会持续3秒，回复效果不会叠加', '施放这个技能需要预判对手的走位，期间宫本为霸体状态不受控制，在对敌方造成伤害后能短时间内回复生命值', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/130/13010.png');",
                "INSERT INTO `skill` VALUES ('1302', '130', '神速', '10', '0', '宫本武藏向前冲刺，对路径上的敌人造成100/130/160/190/220/250（+60%物理加成）点物理伤害；如果穿过敌人将获得抵免100/160/220/280/340/400（+60%物理加成）点伤害的护盾并减少50%神速的冷却时间，护盾每4秒只能获得一次', '利用此技能效果，宫本无论在打野或对拼时都可以用来很好的抵消一部分伤害，注意作为位移逃跑时，就没有护盾效果了哦', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/130/13020.png');",
                "INSERT INTO `skill` VALUES ('1303', '130', '二天一流', '80', '0', '宫本武藏在指定方向范围内选择并锁定目标后跃向空中，在短暂迟延后拔出鸣雷斩落，对目标范围内的敌人造成400/550/700（+100%物理加成）点物理伤害，同时将锁定目标击飞0.75秒并减少其50%移动速度，持续2秒；在接下来的12秒内宫本武藏进入二刀流状态，所有普通攻击和技能都会附带35（+15%物理加成）点法术伤害；狩魔范围提升；神速将获得额外的突进距离', '宫本的大招能够形成非常好的控制效果，并且在开启大招后一段时间内，他的所有技能都有一定的提升，所以在一些情况下，大招先手后进行输出能造成能多的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/130/13030.png');",
                "INSERT INTO `skill` VALUES ('1410', '141', '语·花印', '0', '0', '被动：貂蝉的技能命中会为敌人叠加花之印记，持续8秒，叠加满4层后印记触发，回复自身100点生命，同时会对周围敌人造成160（+64％法术加成）点真实伤害并减少其90％移动速度，持续3秒', '貂蝉每次使用其它技能对敌人造成印记爆炸的时候，爆炸效果会对敌方造成范围伤害并减速，而且该爆炸伤害无视对方护甲防御，是真实伤害.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/141/14100.png');",
                "INSERT INTO `skill` VALUES ('1411', '141', '落·红雨', '5', '50', '貂蝉向指定方向挥出花球，花球飞出一段时间后会返回貂蝉手中，挥出和返回会对路径上的敌人造成180/220/260/300/340/380(+57％法术加成）点法术伤害', '一条直线上造成两次伤害，由于有一定的延迟，所以需要预判对手的走位施放', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/141/14110.png');",
                "INSERT INTO `skill` VALUES ('1412', '141', '缘·心结', '10', '50', '貂蝉瞬间消失并立即出现在指定位置，同时会散发3枚花球攻击附近的敌人，每枚造成140/155/170/185/200/215(+24％法术加成）点法术伤害，如果花球命中敌人，还会减少4秒缘·心结的冷却时间', '貂蝉2技能位移并且对最近的敌人造成3次伤害，如果3次都全命中在同一敌人身上，将会直接触发被动减速，该技能也能用于穿墙逃跑，在没有大招的情况下谨慎使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/141/14120.png');",
                "INSERT INTO `skill` VALUES ('1413', '141', '绽·风华', '40', '80', '貂蝉绽放风华在原地结成法阵，法阵生成及消失时都将造成210/245/280（+50％法术加成）点法术伤害，当貂蝉处于法阵范围内，落·红雨和缘·心结会获得额外的冷却缩减', '大招是貂蝉非常霸道的AOE技能，虽然大招伤害很一般，但能在大范围对敌人触发被动效果，使得貂蝉其它技能造成更多伤害，冷却也更快', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/141/14130.png');",
                "INSERT INTO `skill` VALUES ('1630', '163', '秘剑胧刀', '5', '0', '被动：橘右京将下一次普攻将进行一次强力拔刀斩，对前方敌人造成130%物理加成物理伤害并减少50%移动速度，持续2秒；拔刀斩每5秒可准备1次；对处于攻击边缘的敌人将承受50%的额外伤害.', '每隔一段时间，橘右京的普攻强化，造成高额物理伤害，使得他的对拼能力增强，并且有减速效果，让他能更好的追击敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/163/16300.png');",
                "INSERT INTO `skill` VALUES ('1631', '163', '燕返', '7', '0', '橘右京跃向后方，同时向前方挥刀，对附近的敌人造成200/240/280/320/360/400（+145%物理加成）点物理伤害，若成功命中一名敌方英雄，会减少50%燕返的冷却时间', '这个技能可进可退，既可以用它来进行位移逃跑，也可以用来当作突进技能追击并造成伤害，在线上是一个非常好的消耗敌人的技能，需要注意的是，要通过对局势的判断来选择合适的施法方向打出适当的效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/163/16310.png');",
                "INSERT INTO `skill` VALUES ('1632', '163', '居合', '10', '0', '橘右京向指定方向快速拔刀，对路径上的第一个敌人造成330/375/420/465/510/555（+212%物理加成）点物理伤害，对路上的其余敌人造成的伤害将衰减50%,并将路径末端的敌人眩晕1秒', '这是橘右京技能范围最大的一个技能，具有高额的AOE伤害，是他主要的输出技能，并且还能形成一定的控制效果，将敌人控制住后施放3技能连招，能保证3技能可以足够稳定的命中敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/163/16320.png');",
                "INSERT INTO `skill` VALUES ('1633', '163', '细雪', '12', '0', '橘右京向指定方向连续拔刀四次，每次命中造成100/150/200（+70%物理加成）点物理伤害并且每次命中敌方英雄，自身将回复70/100/130（+35%物理加成）点生命值（命中非英雄单位效果减半）', '这个技能能造成高额的物理伤害，但因为是非指向性技能，所以需要注意判断正确的施法方向，或在敌人被控制后施放效果更佳.对拼时，这个技能在每次命中敌方英雄时，都能回复生命值', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/163/16330.png');",
                "INSERT INTO `skill` VALUES ('1860', '186', '黄金闪闪', '0', '0', '被动：太乙真人附近800范围内的敌方非英雄单位死亡时，太乙真人和附近的队友都会额外获得35％金币', '太乙真人的被动技能使得它成为辅助位置英雄的最佳人选，与队友一起在线上或野区都能为队伍带来更高的经济收益', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/186/18600.png');",
                "INSERT INTO `skill` VALUES ('1861', '186', '意外事故', '5', '65', '太乙真人使用炉子炼制药剂，最多持续5秒，炼制结束时或发生爆炸，对周围敌人造成200（60%法术加成）~600（180%法术加成点法术伤害）点法术伤害并将其晕眩0.5~1.5秒；太乙真人可随时结束炼制并触发爆炸，但炼制时间越长，伤害越高并且晕眩时间越长；炼制时，自身会逐渐增加移动速度，最多增加至30％；同时，太乙真人还会获得60/96/132/168/204/240点物理及法术防御加成；炼制达到最长时间后，若爆炸命中敌方英雄将减少意外事故的全部冷却时间', '这个技能是太乙真人的主要伤害技能，在释放与停止施放时需要根据不同的情况选好时机灵活使用，才能达到很好的效果；在最大时间爆炸时，可以造成最大限度的眩晕效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/186/18610.png');",
                "INSERT INTO `skill` VALUES ('1862', '186', '第三只手', '10', '65', '太乙真人的炉子向指定方向伸出一只触手，会将自身拉向命中的第一个目标；如果是敌方目标，还会造成450/500/550/600/650/700（+80％法术加成）点法术伤害并将其眩晕1秒', '太乙真人可以选择方向对友军或敌军施放，对友军施放是可以迅速靠近友军进行保护，对敌人施放时可造成伤害和控制效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/186/18620.png');",
                "INSERT INTO `skill` VALUES ('1863', '186', '大变活人', '70', '150', '太乙真人召唤替身傀儡，替身傀儡会保护太乙真人和附近血量最低的一名队友，持续3秒；若在傀儡保护下死亡，2秒后将在原地复活，同时将获得2000/2750/3500（+200％法术加成）点生命；复活时，还会对周围的敌人造成600/750/900（+80％法术加成）点法术伤害并减少其50％移动速度，持续2秒；该技能可以在被控制时释放', '这个技能可以保护队友，让队友死而复生，对于爆发型刺客有很好的克制效果，在队友血量较低时开启，会让对方的刺客非常头痛', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/186/18630.png');",
                "INSERT INTO `skill` VALUES ('1540', '154', '长城巡守者', '0', '0', '被动：花木兰使用轻剑时，增加40点移动速度，并且普通攻击和技能伤害将会对敌方英雄叠加平衡印记，持续5秒；叠加满5层后将触发印记，造成200%物理加成点物理伤害，同时将该目标沉默1.5秒并减少其50%移动速度，持续1.5秒；花木兰使用重剑时，普通攻击的基础攻击速度会降低，但造成的伤害会增加50%；并且释放技能过程中将处于霸体状态并减少40%所受到的伤害', '花木兰的双剑形态更倾向于刺客，沉默与减速效果能有效的追击敌方后排和落单英雄/花木兰的重剑形态更倾向于战士，拥有更加强大的输出能力和身体强度.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/154/15400.png');",
                "INSERT INTO `skill` VALUES ('1541', '154', '空裂斩', '8', '0', '花木兰冲锋后向指定方向挥砍，对命中的目标造成80/90/100/110/120/130（+43%物理加成）点物理伤害；如果该技能命中目标，可在5秒内发起第二次空裂斩', '花木兰快速的接近和追击敌人，也可以用来逃离战场/花木兰在重剑形态下的范围输出技能并附带控制，由于需要蓄力，尽可能与队友的控制配合打出高额伤害和控制效果.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/154/15410.png');",
                "INSERT INTO `skill` VALUES ('1542', '154', '旋舞之华', '12', '0', '花木兰向指定方向投掷轻剑，对路径上的敌人造成180/200/220/240/260/280（+90%物理加成）点物理伤害，轻剑会在终点旋转3秒，对范围内敌人每0.5秒造成72（+36%物理加成）点物理伤害并减少50%移动速度，持续1秒；拾起轻剑会减少5秒旋舞之华的冷却时间', '尽可能将技能放在能够伤害到对方并且在追击敌人时自己会经过的路径上，通过多次拾起短剑来更多的使用这个技能打出输出/离花木兰较近的敌方单位会被连续击退.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/154/15420.png');",
                "INSERT INTO `skill` VALUES ('1543', '154', '绽放刀锋', '6', '0', '花木兰拔出重剑横扫，对附近的敌人造成200/280/360（+110%物理加成）点物理伤害，同时增加60点攻击力，持续5秒；拔出重剑后，会使用重剑技能', '在双剑形态下快速接近敌人并在技能进入冷却时开启3技能，配合队友控制利用重剑形态下的技能对敌方造成大量伤害/团战时配合队友利用重剑形态的技能打出输出，在敌人逃跑时使用3技能后用双剑形态的技能追击敌方英雄.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/154/15430.png');",
                "INSERT INTO `skill` VALUES ('1690', '169', '惩戒射击', '0', '0', '后羿的普攻命中敌人后增加自身10%攻击速度，可叠加至多3层。当攻速加成叠加到3层时，后羿的普攻将射出3支箭矢，每支箭矢造成原伤害的40%，强化持续3秒。（期间每次命中刷新持续时间）。', '后羿需要进行三次普攻命中来触发被动效果，一旦触发被动以后，将会造成高额伤害。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/169/16900.png');",
                "INSERT INTO `skill` VALUES ('1691', '169', '多重箭矢', '10', '60', '后羿强化自身攻击，每次攻击造成100/120/140/160/180/200（+80%物理加成）点物理伤害（若触发惩戒射击则每支箭矢造成原伤害的40%）并对面前区域内另外2名敌人造成50%伤害，该效果持续5秒。', '主要输出技能，配合被动效果，每次普攻至多可发射9支箭矢，范围伤害能力极强。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/169/16910.png');",
                "INSERT INTO `skill` VALUES ('1692', '169', '落日余晖', '10', '60', '后羿命令日炙塔对指定区域进行攻击，短暂时间后召唤一束激光打击指定位置。对命中的敌人造成240/280/320/360/400/440（+80%物理加成）点法术伤害和50%减速效果，持续2秒，被中心点命中的敌人将受到额外50%的伤害。', '后羿常规的控制技能，精准打击时也可以提供一定的爆发伤害。必要的时候可以在远处释放该技能骚扰敌人。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/169/16920.png');",
                "INSERT INTO `skill` VALUES ('1693', '169', '灼日之矢', '45', '130', '后羿向前方射出火焰箭。击中敌方英雄时造成500/625/750（+140%物理加成）点物理伤害并晕眩此目标(晕眩时长取决于火焰箭的飞行距离，最多造成3.5秒晕眩)。目标周围的敌人会受到爆炸伤害。', '非常强力的开团技能，一旦命中远处的敌人将造成长时间的晕眩效果。另外这是一个全屏释放的技能，意味着后羿可以随时释放大招支援远处的队友，不过距离较远时，需要对释放方向进行一定的预判。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/169/16930.png');",
                "INSERT INTO `skill` VALUES ('5010', '501', '大吉大利', '0', '0', '被动：明世隐对同一目标的第三次普攻会对其造成0.75秒晕眩，对同一目标叠满印记后3秒内无法再对其叠加印记', '虽然明世隐是辅助，但是在前期对抗时，该被动会对敌人造成极大的威胁', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/501/50100.png');",
                "INSERT INTO `skill` VALUES ('5011', '501', '临卦·无忧', '3', '50', '以法器连接一名队友/敌人，增加/减少75~180物攻和90~215法攻（受法术加成及师卦·飞翼等级影响）及15%移速，强化自身2/3该数值的物攻和法强（不随断链移除）并提升15%移速；若目标是敌人，还会每0.5秒造成100（+20%法术加成）点法术伤害，并对魂链触碰的敌人也造成同等伤害；对目标的属性影响可由师卦·飞翼切换为增加/减少连接的队友/敌人36%-54%物理及法术防御（师卦·飞翼等级影响）；被魂链连接的目标将暴露视野；同时只能连接一个目标', '明世隐的核心技能，进可削弱追杀敌人，退可增强己方核心输出，连接还可解锁大招的释放', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/501/50110.png');",
                "INSERT INTO `skill` VALUES ('5012', '501', '师卦·飞翼', '3', '50', '明世隐切换法器状态，法器状态切换后，临卦·无忧对目标的连接效果也会相应切换；攻击改变状态：使连接的队友/敌人增加/减少其75/96/117/138/159/180(+9%法术加成)物理及90/115/140/165/190/215（+10%法术加成）法术攻击，并强化自身2/3该数值的物理及法术攻击；防御改变状态：增加/减少连接的队友/敌人36%/39%/43%/46%/50%/54%物理及法术防御，并强化自身2/3该数值的物理及法术防御', '当连接到前排坦克时，可以增强/削弱其防御能力，当连接到输出核心时，可增强/削弱其输出能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/501/50120.png');",
                "INSERT INTO `skill` VALUES ('5013', '501', '泰卦·长生', '40', '120', '明世隐在0.75秒吟唱后，法器会带走他的部分生命，化作能量射向当前连接的目标；如果目标是敌人会造成1000/1500/2000(+150%法术加成)点真实伤害，如果是队友会回复等量生命；法球命中目标瞬间，明世隐会损失等量生命值，损失值不会超过当前生命值，且损失生命值会在之后10秒内逐渐恢复；被动：明世隐脱战后每秒回复百分比已损生命值5%', '无论连接敌人还是队友释放，由于自己会损失等量生命值，所以需要释放后能立即脱离战场；再者，因为造成真实伤害，所以对坦克的威胁特别大', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/501/50130.png');",
                "INSERT INTO `skill` VALUES ('1950', '195', '狂热序章', '0', '0', '被动：钩链近端造成100%物理加成点物理伤害，远端额外造成30%伤害。当钩镰远端命中敌人时降低移速，并且自身会获得短暂移速提升，该效果受神乎钩镰等级影响', '使用技能之后普攻三次后再使用技能是玄策的基本操作，击杀或助攻后，获得的超高攻速和移动速度使得玄策十分擅长收割战场', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/195/19500.png');",
                "INSERT INTO `skill` VALUES ('1951', '195', '神乎钩镰', '5', '50', '玄策进行快速位移戏弄对手增加自身25/30/35/40/45/50点物理攻击力，钩镰链中时，玄策进行快速位移会将链中的目标向自己移动方向拉动一小段距离。当钩镰远端命中敌人时，敌人将会减少10%/14%/18%/22%/26%/30%移动速度，自身增加10%/14%/18%/22%/26%/30%移动速度；', '在未链中的情况下使用，可以快速的位移寻找切入位置或者躲避技能，在链中的情况下使用，可以强制让敌人向着玄策位移一段距离', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/195/19510.png');",
                "INSERT INTO `skill` VALUES ('1952', '195', '梦魇钩锁', '10', '70', '玄策舞动钩镰，蓄力后将钩镰向指定方向甩出，钩镰会对路径上的敌人造成300/340/380/420/460/500(+140%额外物理加成)点物理伤害并将捆绑在回收过程中命中的第一个目标，同时对捆绑目标施加狩猎效果；如果捆绑住目标后再次释放该技能，玄策会将目标甩向身后并失去狩猎效果;蓄力越久，钩镰飞行距离越远，蓄力最多持续4秒；蓄力时，自身将增加30%移动速度；取消蓄力会减少梦魇钩锁50%的冷却时间；玄策和狩猎目标作战时将增加10%/12%/14%/16%/18%/20%伤害并减少20%/22%/24%/26%/28%/30%该目标对自己的伤害；狩猎目标死亡，将立刻返还梦魇钩锁80%的冷却时间', '开始蓄力时，玄策的移动速度会增加，并且会对敌方走位进行压迫，命中后配合玄策不同技能组合可以打出多种的效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/195/19520.png');",
                "INSERT INTO `skill` VALUES ('1953', '195', '瞬镰闪', '2', '80', '玄策沿钩镰锁链瞬镰闪到对方身后，对路径的的敌人造成350/525/700（+200%额外物理加成）点物理伤害；移动过程中，玄策挥舞短镰，将会减少50%所受到的伤害；瞬镰闪只有在钩镰捆绑住目标时才能释放；被动:玄策助攻或击杀死英雄都会激发心中的狂血使得移速增加70%（随时间效果衰减），攻速增加75%，持续5秒', '冷却较短的瞬镰闪使得玄策无比灵活，利用瞬镰闪快速移动至合适位置，将目标投掷到危险地带或者直接突袭敌人都是不错的选择', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/195/19530.png');",
                "INSERT INTO `skill` VALUES ('1790', '179', '辉煌指引', '0', '0', '被动：女娲每升一级提升3%自身视野（最高42%），同时提升2%普攻与技能释放范围（最高28%）；女娲的技能伤害会标记英雄，使得友军（包括自己）对标记目标造成的普攻和技能伤害，能够在2秒内增加友军16%移动速度和生命回复（总计可回复40（+12%法术加成）），这个效果对于每个攻击者每0.5秒只能触发一次；', '越到后期，女娲的视野范围更广阔，配合超远的法术攻击距离，让其在后期总能够找到合适的作战位置', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/179/17900.png');",
                "INSERT INTO `skill` VALUES ('1791', '179', '指令：放射', '7', '60', '女娲向指定方向释放能量，对路径上的敌人造成250/290/330/370（+36%法术加成）点法术伤害和击退效果，在撞到敌人之后逐渐停下，并展开十字型阵列，对命中的敌人造成250/290/330/370（+36%法术加成）点法术伤害；', '击退后所展开的十字阵列范围颇大，但是能力飞行时速度较慢，需要一定预判', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/179/17910.png');",
                "INSERT INTO `skill` VALUES ('1792', '179', '指令：创造', '0', '40', '女娲在指定位置创造矩阵空间，阻止敌方单位通过；创造的空间将在3秒后消失，空间消失或者女娲其他技能触碰到矩阵空间都会引发爆炸对附近的敌人造成500/580/660/740（+80%法术加成）点法术伤害；女娲每100点法术攻击将会延长矩阵空间的0.2秒持续时间，最多可储存4个矩阵空间；', '女娲的法术强度可以延长矩阵的存在时间，以便女娲创造更加有利的作战环境', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/179/17920.png');",
                "INSERT INTO `skill` VALUES ('1793', '179', '指令：迁跃', '50', '100', '女娲创造裂隙，在短暂的吟唱后将自己传送到指定位置，并对周围的敌人造成500/560/620/680（+65%法术加成）点法术伤害并短暂眩晕；被动：女娲受到英雄伤害会触发自动防御机制，对身边敌人造成50%减速，生成可抵挡400/480/560/640（+80%法术加成）点伤害的护盾并增加30%移动速度持续2秒', '超远程移动技能，可以保证女娲对于其他分路的战斗和野区遭遇战的支援速度', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/179/17930.png');",
                "INSERT INTO `skill` VALUES ('1794', '179', '指令：毁灭', '50', '120', '女娲在短暂的延迟后，向指定方向释放纯净的能量，对路径上的敌人造成700/1050/1400（+88%法术加成）点法术伤害', '中前期，手握技能就可以对其他位置的敌人进行威慑，后期更是可以直接对敌方后排进行打击', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/179/17940.png');",
                "INSERT INTO `skill` VALUES ('1520', '152', '冰封之心', '0', '0', '被动：王昭君脱离战斗后会获得可抵免450（+52%法术加成）点伤害的寒冰护盾，护盾破裂时对附近敌人造成一次冰霜冲击造成450（+52%法术加成）点法术伤害并减少其50%移动速度，持续1秒；寒冰护盾每3秒最多获得1次', '护甲破裂时将对附近敌人造成一次冰霜冲击，及时衔接技能配合2技能的被动效果，打敌人个措手不及！', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/152/15200.png');",
                "INSERT INTO `skill` VALUES ('1521', '152', '凋零冰晶', '5', '80', '王昭君在指定位置操控碎裂冰晶绽开，对范围内的敌军造成400/480/560/640/720/800（+65%法术加成）点法术伤害并减少其50%移动速度，持续2秒，同时还会获得敌人的视野', '这个技能CD很短，而且是AOE伤害伴有减速效果，在对线和团战前都是很好的消耗技能，同时能提高王昭君2技能的命中率', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/152/15210.png');",
                "INSERT INTO `skill` VALUES ('1522', '152', '禁锢寒霜', '8', '80', '王昭君在指定区域引领寒霜之力，短暂延迟后将范围内敌人并造成250/280/310/340/370/400（+47%法术加成）点法术伤害并将其冰冻2.5秒；被动：王昭君对冰冻的敌人会额外造成250/280/310/340/370/400（+50%法术加成）点法术伤害。', '这个技能能够触发王昭君的被动，但是有一定延时，需要预判对手的走位，在友方控制敌人后施放可极大提升命中率哦', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/152/15220.png');",
                "INSERT INTO `skill` VALUES ('1523', '152', '凛冬已至', '35', '120', '王昭君立于原地引导寒冬之力，在指定位置降下暴风雪，对范围内敌人每0.5秒造成280/390/500（+35%法术加成）点法术伤害，持续4.7秒；被暴风雪命中的敌人会减少30%移动速度，持续1秒；并且开启引导时，自身会获得寒冰护盾，同时获得600点物理防御', '王昭君最具有输出的技能，施放时获得600护甲，因为技能持续时间很长，范围很大，选择一个很好的位置施放大招是最关键的，最好的情况是，保证打到更多的敌人，而敌人又不容易近身切入', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/152/15230.png');",
                "INSERT INTO `skill` VALUES ('1500', '150', '杀意之枪', '0', '0', '被动：韩信的第四次普通攻击会将敌人击飞0.5秒；韩信释放技能命中目标后会增加50％攻击速度', '拥有一定的控制性可以在一定的时候打断敌方连续攻击触发的技能，挑起后是一种空挡状态可以进行技能的连接.短暂的攻击速度加成有助于让韩信更好的触发被动.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/150/15000.png');",
                "INSERT INTO `skill` VALUES ('1501', '150', '无情冲锋', '9', '55', '韩信持枪向指定目标周围发起冲锋，冲锋会对范围内目标造成135/151/167/183/199/215（+62％物理加成）点物理伤害并将范围内敌人击飞0.8秒；冲锋后5秒内可发动第二次冲锋，但不会击飞敌人', '两段的位移和一次击飞是韩信的主要技能，它让韩信进可攻退可跑.一起用是一大段的位移，分开用利于触发被动，开头的控制让大招更容易全部命中.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/150/15010.png');",
                "INSERT INTO `skill` VALUES ('1502', '150', '背水一战', '5', '80', '韩信向后跳跃并使下一次普通攻击变更为横扫攻击，持续3秒，横扫会对范围内敌人造成180/200/220/240/260/280(+100％物理加成）点物理伤害', '一次位移和一次更高的伤害，与无情冲锋配合起来可以进行快速的支援和打击.同时，CD较短，进行撤退也会很实用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/150/15020.png');",
                "INSERT INTO `skill` VALUES ('1503', '150', '国士无双', '36', '140', '韩信朝指定方向乱舞四次长枪，对命中的敌人每次造成130/165/200(+62％物理加成）点物理伤害；最后一次攻击会将范围内的敌人击飞0.5秒；乱舞期间韩信处于霸体状态', '提供一次击飞，与隐性的霸体(虽然会被一定的技能打断但打断后是无冷却的)抵挡一定的控制效果.突进敌阵用大招进行扰乱敌方阵型也是不错的选择.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/150/15030.png');",
                "INSERT INTO `skill` VALUES ('1830', '183', '真神觉醒', '0', '0', '被动：雅典娜死亡后，将在当前位置觉醒真身并且可以真身形态继续移动；双方均可观察到真身的存在；复活时，雅典娜将在真身位置出现；向着雅典娜移动的友军英雄将会增加20％移动速度；作为造物主的古神，雅典娜对野怪和小兵会额外造成25％的伤害', '雅典娜在死亡后可以充当一个移动的视野侦查，为队友提供视野，在复活时一定要选择好复活位置，以免被敌方再次击杀.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/183/18300.png');",
                "INSERT INTO `skill` VALUES ('1831', '183', '神圣进军', '6', '60', '雅典娜举盾向指定方向突进，对路径上的敌人造成290/350/410/470/530/590（+63％物理加成）点法术伤害并将其击退，同时获得一个可抵免375/465/555/645/735/825（+82％物理加成）点伤害的护盾，持续1秒；护盾存在时，雅典娜免疫所有控制效果', '这个技能可以使雅典娜进行一个较短距离的突进，对路径上的敌人造成击退效果，能同时生成护盾抵挡伤害，如果护盾被打破，那么雅典娜会受到眩晕效果，所以需要灵活使用这个特性.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/183/18310.png');",
                "INSERT INTO `skill` VALUES ('1832', '183', '贯穿之枪', '6', '60', '雅典娜向指定方向跳跃，同时将下一次普通攻击变更为一个枪刺，枪刺可造成370/430/490/550/610/670（+100％物理加成）点物理伤害；连续三次对敌方英雄枪刺将贯穿该名英雄，造成目标已损生命25％的物理伤害，枪刺间隔不超过5秒才能触发贯穿；神圣进军和敬畏圣盾命中敌人时贯穿之枪会减少4秒的冷却时间', '这个技能是雅典娜重要的突进技能，主动使用技能位移后使用普攻可以再次突进造成枪刺伤害，逃命时也可以使用技能进行位移逃跑哦！', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/183/18320.png');",
                "INSERT INTO `skill` VALUES ('1833', '183', '敬畏圣盾', '15', '90', '雅典娜立于原地并举起圣盾，自身获得一个巨大的可抵免1100/1650/2200(+150％物理加成）点伤害护盾，持续1.5秒；1.5秒后圣盾碎裂对范围内敌人造成450/650/850（+150％物理加成）点法术伤害并减少50％移动速度，持续1秒；护盾存在时，雅典娜免疫所有控制效果', '这个技能产生的护盾能吸收大量的伤害，并且效果结束后造成AOE伤害；但需要注意的是，这个技能最好是配合突进技能进场后在团战混战中心释放能吸收大量伤害和大范围的减速效果.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/183/18330.png');",
                "INSERT INTO `skill` VALUES ('1970', '197', '气合', '90', '0', '被动：受到致命伤害时，弈星会遁入虚无，免疫所有伤害并提升30%移动速度，持续1秒，并在旁边自动生成两颗棋子，这个效果有90秒CD。弈星的普通攻击会造成70（+100%物理加成）（+20%法术加成）点法术伤害。', '凭借被动，弈星很难被刺客一击击杀，但是单人行动时，被动并不能让弈星摆脱危险情况', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/197/19700.png');",
                "INSERT INTO `skill` VALUES ('1971', '197', '定式·镇神', '1', '35', '在指定地点放置一颗黑色棋子，棋子生成时会对周围敌人造成200/240/280/320/360/400（+16%法术加成）点法术伤害。黑白棋子如果距离低于1000，则会互相吸引并爆炸，对范围内的敌人造成400（+32%法术加成）点法术伤害和90%减速持续1秒(爆炸伤害随英雄等级成长,2秒内连续受到爆炸后续伤害将衰减30%)。棋子每8/7.6/7.2/6.8/6.4/6秒存储一发（受到减CD属性影响），最多存储4颗，每颗棋子最多持续15秒。被动：棋子被动爆炸命中敌方英雄后会提高弈星30/60/90/120/150/180法术强度（随1技能等级成长），持续3秒，最多叠加4层', '可以先放置多颗同色棋子，再根据实际情况使用另一色棋子引动爆炸', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/197/19710.png');",
                "INSERT INTO `skill` VALUES ('1972', '197', '定式·倚盖', '1', '35', '在指定地点放置一颗白色棋子，棋子生成时会对周围敌人造成200/240/280/320/360/400（+16%法术加成）点法术伤害。黑白棋子如果距离低于1000，则会互相吸引并爆炸，对范围内的敌人造成400（+32%法术加成）点法术伤害和90%减速持续1秒(爆炸伤害随英雄等级成长,2秒内连续受到爆炸后续伤害将衰减30%)。棋子每8/7.6/7.2/6.8/6.4/6秒存储一发（受到减CD属性影响），最多存储4颗，每颗棋子最多持续15秒。被动：棋子被动爆炸命中敌方英雄后会提高弈星8%/16%/24%/32%/40%/48%攻速（随2技能等级成长），持续3秒，最多叠加4层', '根据形势让敌人处于棋子连线的中央可打出高额伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/197/19720.png');",
                "INSERT INTO `skill` VALUES ('1973', '197', '天元', '80', '120', '弈星划出一个巨大的虚空棋盘，棋盘上会自动生成4颗棋子。释放技能2秒后棋盘完全成型，成型时对棋盘内的敌人造成480/720/960（+72%法术加成）点法术伤害和0.5秒眩晕，棋盘完全成型后所有棋盘上的敌人将无法离开棋盘范围，棋盘成型后共持续4秒。', '超大的范围是该技能的优势，但是略微有些长的生成时间，需要配合队友控制才能将敌人完全限制在棋盘之中', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/197/19730.png');",
                "INSERT INTO `skill` VALUES ('1750', '175', '制裁仪式', '0', '0', '被动：钟馗受到单次伤害超过10%当前生命值会引发爆炸，对自身附近的敌人造成60（+60%法术加成）（+102)（最大生命值3%）法术伤害，引发爆炸有2秒冷却时间；钟馗死亡后3秒引爆自身，对自身附近的敌人造成240（+240%法术加成）（+410）（最大生命值12%）法术伤害.', '这个被动技能让钟馗能够无畏的担任起坦克和人肉炸弹的职责，团战可以冲进最密集的交战区域', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/175/17500.png');",
                "INSERT INTO `skill` VALUES ('1751', '175', '虚空清道者', '4', '60', '钟馗以虚空之力锤击地面，对范围内的敌人造成250/300/350/400/450/500（+45%法术加成）点法术伤害并减少30%移动速度，持续1秒；在钟馗身边的敌人受到250%伤害并减少30%移动速度，持续2秒', '软性控制技能，减速范围内的敌人，技能CD较短，可以多次施放', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/175/17510.png');",
                "INSERT INTO `skill` VALUES ('1752', '175', '湮灭之锁', '17', '60', '钟馗向指定方向投出钩锁并将第一个命中敌人拉至身前，钩锁命中将造成800/900/1000/1100/1200/1300（+100%法术加成）点法术伤害，同时为该敌人添加标记，轮回吞噬的每次吞噬会将标记的敌人眩晕0.5秒；被动：钟馗每次击败英雄或助攻会获得1层虚空噬魂。每层虚空噬魂会增加180最大生命值，最多叠加20层', '该技能能在很远的位置将对方敌人拉到自己身边，将其秒杀，注意技能有一定前摇，需要预判对手的走位，也可以后手勾，这样能大大提高命中率哦', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/175/17520.png');",
                "INSERT INTO `skill` VALUES ('1753', '175', '轮回吞噬', '40', '130', '钟馗蓄力后向指定方向张开虚空身躯，对范围内的敌人进行多次吞噬效果，总共造成2400/3000/3600（+360%法术加成）点法术伤害；钟馗在释放同时会获得可抵免240点伤害护盾', '这个技能控制效果十足，命中敌人越多，该效果的伤害收益越大，但有一定施法前摇，需要一定的预判技巧', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/175/17530.png');",
                "INSERT INTO `skill` VALUES ('1990', '199', '晚云落', '0', '0', '被动：阿离的普攻伤害会为目标叠加枫之印记，每四层印记触发一次爆炸（并叠加新的印记），对周围敌人造成150（+70%物理加成）点法术伤害并减少所有技能1秒CD；释放技能或收回纸伞后阿离的普通攻击将会额外投掷一枚飞镖并造成30%物理加成点物理伤害；阿离能够再释放技能后再次使用技能回归到纸伞的位置；当阿离手中没有纸伞时，自身移动速度提升50点与基础攻击间隔缩短20%', '阿离每次释放技能下次普通攻击可叠加2个印记，通过不断的释放技能快速累积标记即可打出爆炸伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/199/19900.png');",
                "INSERT INTO `skill` VALUES ('1991', '199', '岑中归月', '10', '50', '阿离向指定方向瞬步，并将纸伞留在原地（如果纸伞在阿离手中）', '快速位移技能，即可留作生存手段，亦可先手打出小爆发', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/199/19910.png');",
                "INSERT INTO `skill` VALUES ('1992', '199', '霜叶舞', '12', '60', '阿离旋转起操控纸伞飞旋（如果纸伞在阿离手中），击落飞行物对范围内敌人造成两段打击每段造成120/144/168/192/216/240（+40%物理加成）点法术伤害；', '需要注意纸伞将做圆周飞行，配合走位，可移动超乎想象的距离，进一步的增加了阿离的灵活性，并且击落飞行物将会保证阿离一定的生存空间', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/199/19920.png');",
                "INSERT INTO `skill` VALUES ('1993', '199', '孤鹜断霞', '30', '90', '阿离击退前方的敌人，造成250/375/500（+80%物理加成）点法术伤害，并将纸伞向指定方向掷出（如果纸伞在阿离手中）', '掷出纸伞距离比较远，可在作逃脱或跨地形作战时使用，并且纸伞不在手中时，也可击退敌人并造成伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/199/19930.png');",
                "INSERT INTO `skill` VALUES ('1760', '176', '惊鸿调', '1', '0', '被动：杨玉环切换曲调，在释放技能时产生不同的效果；破阵：释放技能对800范围内敌方英雄造成75（+15%法术加成）点法术伤害；对小兵额外造成75%伤害；清平：释放技能对800范围内友军造成50（+10%法术加成）点生命回复', '被动技能可以切换状态！根据战况，点击切换不同的曲调，在每次释放技能时会附带治疗或伤害效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/176/17600.png');",
                "INSERT INTO `skill` VALUES ('1761', '176', '霓裳曲', '5', '50', '杨玉环拨动琴弦，产生3道音波，音波在向外飞出后会缓慢追击敌方单位（英雄优先）造成200/240/280/320/360/400（+33%法术加成）点法术伤害并减少目标50%移动速度，持续1秒；释放技能后，杨玉环的下一次普通攻击将会造成额外225/270/315/360/405/450（+36%法术加成）点法术伤害，并减少所有技能CD1秒；', '杨玉环使用频率最高的技能，不仅附带减速效果，随后的普通攻击能为杨玉环带来更快的技能冷却效果；配合频繁的触发被动，使得敌人在被动范围内将会不断被消耗并提供队友高额的回复能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/176/17610.png');",
                "INSERT INTO `skill` VALUES ('1762', '176', '胡旋乐', '15', '70', '杨玉环拨动琴弦短时间提升自身50%移速(受时间衰减)，1.5秒后对位于杨玉环600~800范围内的敌人造成250/300/350/400/450/500（+40%法术加成）点法术伤害与晕眩效果持续0.75秒', '因为只有外环区间才能将敌方眩晕，所以需要利用技能附带的移动速度进行走位，最大的发挥其功用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/176/17620.png');",
                "INSERT INTO `skill` VALUES ('1763', '176', '长恨歌', '65', '100', '杨玉环解放力量，跃向空中（期间无法被选中）演奏乐曲；演奏完成后治疗身边500范围内的友军750/1100/1450（+80%法术加成）点生命，并对范围内敌人造成500/750/1000（+75%法术加成）点法术伤害；', '跃向空中时，杨玉环能够躲避一波敌人的进攻，玩家需要准确的把握释放的时机，将战局扭转', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/176/17630.png');",
                "INSERT INTO `skill` VALUES ('5020', '502', '寸劲', '0', '0', '被动：在人形态下每次普攻将发出气功波，攻击目标时造成额外20点法术伤害（伤害随英雄等级提升），额外造成的法术伤害可以在3秒内进行叠加，至多提升到基础值的3倍。普攻每次命中恢复5点能量', '裴擒虎在人形态进行攻击能更快的恢复能量，为下次切换虎形态打爆发做铺垫/裴擒虎在虎形态拥有更高的基础属性和更强的正面战斗技能机制，但能量恢复较慢，需要与人形态下的特性互补', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/502/50200.png');",
                "INSERT INTO `skill` VALUES ('5021', '502', '冲拳式', '8', '50', '朝指定方向发出气功，对一条路径上的敌人造成250/300/350/400/450/500（+115%额外物理加成）点法术伤害和目标当前生命值8%的额外法术伤害并减少目标50%移动速度，持续1.5秒。该技能命中敌方英雄时恢复20点能量', '该技能兼顾消耗和控制效果，并且攻击距离较远，可以在远处不断骚扰敌人/裴擒虎的斩杀技能，较短的冷却时间使得其拥有不弱于其他刺客强势收割能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/502/50210.png');",
                "INSERT INTO `skill` VALUES ('5022', '502', '气守式', '8', '50', '施展念气环绕自身，施法瞬间对附近敌人造成120/176/232/288/344/400（+64%额外物理加成）点法术伤害，并形成护盾包裹自身持续5秒，可吸收400（+150%额外物理加成）伤害。护盾存在期间每0.5秒对附近敌人造成30/44/58/72/86/100（+16%额外物理加成）点法术伤害并提升裴擒虎30%攻击速度。', '需要注意的是念气护盾不会因为切换形态而消失/当在野区等复杂地形作战时，裴擒虎通过折返跳跃的技巧可以打出超额的伤害，也可通过翻山越岭灵活躲避敌人的追捕', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/502/50220.png');",
                "INSERT INTO `skill` VALUES ('5023', '502', '虎啸风生', '7', '0', '裴擒虎切换到虎形态，1秒内增加30%移动速度并强化下两次攻击，第一击扑向指定目标，造成20/110/200/290（+100%物理加成）点物理伤害和1秒90%减速。第二击对面前的敌人造成80（+100%物理加成）点法术伤害', '裴擒虎的核心技能，让他从1级开始就拥有双形态以及2套完全不同的技能。需要根据当前的对战情况选择合适的形态', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/502/50230.png');",
                "INSERT INTO `skill` VALUES ('1350', '135', '陷阵之志', '90', '0', '被动：项羽生命低于30%时，自身减少40%所受到的伤害，持续6秒，每90秒最多触发一次；项羽的普通攻击会对目标额外造成其6%最大生命值比例的物理伤害（对小兵/野怪造成300点物理伤害），每个目标每6秒只能触发一次', '项羽的生命值低于30%时，会持续回复生命值，最大生命值越高，回复血量越多', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/135/13500.png');",
                "INSERT INTO `skill` VALUES ('1351', '135', '无畏冲锋', '14', '50', '项羽向指定方向冲撞，冲撞时触碰到的所有敌人会被一起击退，冲撞结束时项羽还会向前方发动一次挑斩，挑斩会将敌人击飞0.5秒，冲撞和挑斩都会对敌人造成120/144/168/192/216/240（+48%物理加成）点物理伤害，技能命中敌方会减少30%无畏冲锋的冷却时间', '项羽的突进和控制技能，敌方多个英雄站在路径上可以控制一群人，这个技能也能在敌方刺客切入后将其推开保护队友，同时它也可以当作一个位移技能来使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/135/13510.png');",
                "INSERT INTO `skill` VALUES ('1352', '135', '破釜沉舟', '7', '40', '项羽发出怒吼，对附近范围内的敌人造成150/180/210/240/270/300（+80%物理加成）点法术伤害，并且会减少30%的移动速度和25/28/31/34/37/40%的伤害输出，持续2秒； 被动：项羽的每1点物理防御会额外增加1.5-2.5点最大生命值（随技能等级成长）', '群体控制技能，控制范围内的敌人，可以配合1技能打出2段控制效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/135/13520.png');",
                "INSERT INTO `skill` VALUES ('1353', '135', '霸王斩', '50', '100', '项羽朝指定方向蓄力后斩出一道刀气，对路径上的敌人造成540/810/1080（+96%物理加成）点物理伤害，对被枪刃命中的敌人会造成额外66%伤害并将其眩晕1秒；被动：项羽会对等级低于或等于自身的目标造成额外15%的伤害', '这是个附带真实伤害的AOE技能，但有一定的延迟时间，技能范围较大，尽量打到更多的人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/135/13530.png');",
                "INSERT INTO `skill` VALUES ('1820', '182', '比翼同心', '0', '0', '被动：释放飞剑技能时，莫邪都会配合干将飞出自己的雌剑，雌剑和雄剑呈对称状在一点交汇，造成同等伤害。干将莫邪普通攻击使用飞剑挥砍对方造成300（+25%法术加成）点法术伤害', '相比其他英雄，干将虽然弹道比较独特，但凭借被动会拥有更大的覆盖范围，并且在交汇点将能够造成更多伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/182/18200.png');",
                "INSERT INTO `skill` VALUES ('1821', '182', '护主邪冢', '12', '80', '干将连续两次将剑冢用力向前推刺出并自身后移，剑冢造成300/350/400/450/500/550（+50%法术加成）点法术伤害并击飞击退敌人。剑冢击伤的敌人3秒内降低150/180/210/240/270/300点法术防御。被动：每次击败英雄或助攻，剑冢都为干将增加自身15点永久法术强度，最高20层。', '干将的自保技能，由于还可调整自身位置，足够拉开与敌人的作战距离', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/182/18210.png');",
                "INSERT INTO `skill` VALUES ('1822', '182', '雌雄双剑·近', '9', '45', '凌空成剑，释放雄剑，雄剑沿着曲型弹道飞行对敌方造成400/485/570/655/740/825（+40%法术加成）点法术伤害。', '虽然干将需要维持作战距离，但当近战突进至身前时，该技能进行一定程度上的反击', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/182/18220.png');",
                "INSERT INTO `skill` VALUES ('1823', '182', '雌雄双剑·远', '9', '45', '凌空成剑，释放雄剑，雄剑沿着曲型弹道飞行对敌方造成400/485/570/655/740/825（+40%法术加成）点法术伤害。', '释放距离一直都是干将的优势所在，该技能为干将的主要进攻消耗方式', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/182/18230.png');",
                "INSERT INTO `skill` VALUES ('1824', '182', '剑来', '40', '120', '干将立刻刷新飞剑技能，并唤出更多的剑来强化下一次飞剑技能。被动：莫邪时刻观察着战场，将增加15%视野距离，每一股飞剑命中敌人减少剑来一秒CD。', '使用2、3技能后立即刷新技能冷却，可连续对一定距离上的敌人攻击2次，打出爆发性伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/182/18240.png');",
                "INSERT INTO `skill` VALUES ('1870', '187', '暗冕之噬', '0', '0', '被动：东皇太一使用祭典创造出来的黑暗能量体极不稳定，敌人被黑暗能量体触碰到都会造成100（+敌方3%最大生命值）（50%法术加成）点法术伤害；每当黑暗能量体对敌方英雄造成伤害时，东皇太一将会回复100（自身最大生命3%）（+40%法术加成）点生命值，对敌人非英雄目标造成伤害时，东皇太一将会回复自身1%最大生命值', '尽可能地去靠近敌方英雄，利用黑暗能量体吸取敌方生命', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/187/18700.png');",
                "INSERT INTO `skill` VALUES ('1871', '187', '日蚀祭典', '5', '60', '东皇太一释放力量，对周围造成300/360/420/480/540/600（+40%法术加成）点法术伤害并创造出黑暗能量体在身边环绕，同时最多存在3个；释放能量时，若对范围内敌人造成伤害，则会减少50%日蚀祭典的冷却时间；黑暗能量体的数量会影响曜龙烛兆的威力', '尽可能地保持时刻有三层黑暗能量体，来对敌方造成高额输出和吸血', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/187/18710.png');",
                "INSERT INTO `skill` VALUES ('1872', '187', '曜龙烛兆', '6', '75', '东皇太一每拥有1个黑暗能量体，就可召唤1条曜龙对指定方向进行范围冲击，每次冲击将造成500/600/700/800/900/1000（+60%法术加成）点法术伤害；第一次和第二次冲击范围内的敌人减少50%和90%移动速度，持续1秒；第三次冲击范围内的敌人将被眩晕1秒；释放时，自身将增加30%移动速度，持续1秒，并且每增加1个黑暗能量体将额外增加10%移动速度', '三颗黑暗能量体时释放效果最强，并且由于技能释放延迟大，建议配合队友的控制使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/187/18720.png');",
                "INSERT INTO `skill` VALUES ('1873', '187', '堕神契约', '50', '100', '东皇太一压制敌方英雄，同时在彼此间创造一个契约链接，持续2.5秒；当契约的双方的一方受到伤害时，另一方会受到同等的伤害；在强大的堕神契约下双方无法进行任何操作，也无法净化；堕神契约强大的能量溢出，东皇太一将直接获得三个黑暗能量体', '东皇太一的三技能是强力控制，但输出不足，需要队友在他控制住敌人时尽快击杀敌方，并且开启大招时自己和敌方都会受到同样的伤害，要注意自己的血量', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/187/18730.png');",
                "INSERT INTO `skill` VALUES ('1100', '110', '王者审判', '0', '0', '被动：嬴政的普通攻击可以击穿目标，并造成70（+100%物理加成）（+30%法术加成）点法术伤害；攻击机关时会衰减50%的伤害', '前期在清理兵线时有较大作用而且能神不知鬼不觉的消耗对手', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/110/11000.png');",
                "INSERT INTO `skill` VALUES ('1101', '110', '王者惩戒', '8', '100', '嬴政在指定目标区域召唤黄金剑阵，持续2.5秒，每0.5秒对范围内的敌人造成100/120/140/160/180/200（+27%法术加成）点法术伤害；范围内敌人会减少10%移动速度，持续1秒，最多叠加5层', '嬴政主要输出和伤害技能，但有一定的延迟效果，所以需要准确的预判敌人走位', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/110/11010.png');",
                "INSERT INTO `skill` VALUES ('1102', '110', '王者守御', '12', '40', '嬴政开启王之护盾，持续时间内增加自身140/168/196/224/252/280法术攻击力和10/11/12/13/14/15%移动速度；被动：增加嬴政自身70/84/98/112/126/140法术攻击力和2/3/3/4/4/5%移动速度', '在使用其他输出技能前开启，能增强其它技能输出能力，并且护盾效果也能提升一定的自保能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/110/11020.png');",
                "INSERT INTO `skill` VALUES ('1103', '110', '至尊王权', '30', '150', '嬴政展示王者权力，增加20%移动速度，持续5秒，期间将会号令55只飞剑持续向指定方向进行冲击，每只飞剑能造成70/90/110（+8%法术加成）点法术伤害，该技能会对小兵和野怪造成25%额外伤害', '霸气无比的伤害技能，伤害极高，使用过程中嬴政可以移动，不断调整位置扫射敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/110/11030.png');",
                "INSERT INTO `skill` VALUES ('1320', '132', '连锁反应', '0', '0', '被动：马可波罗的普攻与技能伤害能够破坏目标的防御并回复50点能量，连续10次受到伤害后，目标每次受到马可波罗的伤害都会额外受到90（+30%物理加成）点真实伤害(该伤害可以暴击)，持续5秒', '马可波罗可以预留技能在触发真实伤害后打出爆发伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/132/13200.png');",
                "INSERT INTO `skill` VALUES ('1321', '132', '华丽左轮', '7', '70', '马可波罗向指定方向连续射击过程期间获得10%移速提升，每一枪造成140/154/168/182/196/210（+12%物理加成）点物理伤害都能触发普攻的法球效果，命中回复10点能量，额外攻击速度会影响技能射出的子弹数目，在攻速达到0/75%/150%时射出子弹数5/7/9', '射击时获得的移速提升会让马可波罗轻松游走在敌人的攻击边缘，从而掌握主动', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/132/13210.png');",
                "INSERT INTO `skill` VALUES ('1322', '132', '漫游之枪', '5', '70', '马可波罗向指定方向闪烁，立即出现在目标位置；被动：马可波罗身边500范围内存在敌方英雄时，提升10%/12%/14%/16%/18%/20%的伤害和15%/18%/21%/24%/27%/30%移动速度', '仔细分析敌人技能状态后，熟练的马可波罗会主动靠近对手获得额外的伤害移速加成', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/132/13220.png');",
                "INSERT INTO `skill` VALUES ('1323', '132', '狂热弹幕', '50', '70', '马可波罗向指定方向闪烁，立即出现在指定位置并向周围发射弹幕触发普攻的法球效果，造成120/180/240（+15%物理加成）点物理伤害，额外攻击速度会影响技能射出的弹幕波数，在攻速达到0/75%/150%时射出弹幕数10/13/16；', '大招可以造成大量范围伤害，但需要注意的是释放大招时马可波罗进行移动会立即打断大招释放', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/132/13230.png');",
                "INSERT INTO `skill` VALUES ('1310', '131', '侠客行', '0', '0', '被动：李白使用普通攻击攻击敌人时，会积累1道剑气，持续3秒；积累4道剑气后进入侠客行状态，增加5%物理攻击力并解除青莲剑歌的封印，持续5秒；攻击建筑不会积累剑气', '李白的大招能否施放的关键，普攻出第四下后触发侠客行，还能提升攻击力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/131/13100.png');",
                "INSERT INTO `skill` VALUES ('1311', '131', '将进酒', '12', '80', '李白可用醉剑式向指定方向连续突进2次，对路径上的敌人造成120/144/168/192/216/240（+100%物理加成）点物理伤害并且造成晕眩0.5秒；第三次释放会回到原地；每次释放间隔不能超过5秒', '一定要控制好CD时间，这是李白对敌方进行消耗的主要技能，起到杀人红尘中，脱身白刃里的效果.当然该技能可突进可逃跑，注意灵活使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/131/13110.png');",
                "INSERT INTO `skill` VALUES ('1312', '131', '神来之笔', '12', '60', '李白以自身为中心化剑为青莲剑阵，对范围内敌人造成110/142/174/206/238/270（+54%物理加成）点物理伤害；对触碰到剑圈的敌人造成180（+90%物理加成）点物理伤害并且减少90%移动速度，持续1秒；同时敌人会减少100/175/250/325/400/475点物理防御，持续3秒；李白释放技能期间不可被选中', '该技能用于造成伤害和减速敌人，敌人触碰剑圈还能减少敌人护甲，这是李白造成高额输出的前提', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/131/13120.png');",
                "INSERT INTO `skill` VALUES ('1313', '131', '青莲剑歌', '12', '120', '李白化身为剑气，对指定方向范围内的所有敌人飞速穿梭斩击5次，每次斩击造成150/215/280（+68%物理加成）点物理伤害；当同时攻击多个敌方英雄时，每增加一位敌方英雄将衰减15%的伤害，最低衰减至初始伤害的70%；青莲剑歌需要由侠客行解除限制后方可释放，使用后立即进入限制状态；李白释放技能期间不可被选中和攻击', '这是李白团战造成超高AOE伤害的关键，同时也可以用来躲避敌方技能，防止被秒', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/131/13130.png');",
                "INSERT INTO `skill` VALUES ('1400', '140', '一骑当千', '0', '0', '被动：关羽每主动移动100距离将增加2％移动速度；持续移动达到2000距离时会进入冲锋姿态；当关羽的移动速度被减少至375点以下或受到控制效果时将退出冲锋姿态；关羽面朝敌方移动时将增加20％移动速度；冲锋姿态：关羽的普通攻击会击退敌人并附带10％最大生命值的物理伤害', '关羽需要尽量通过一直移动来触发被动的冲锋状态，这样才能增加自己的普攻输出和造成击退效果，由于是最大生命值的百分比伤害，所以关羽在装备选择上尽量选择肉装和移速装', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/140/14000.png');",
                "INSERT INTO `skill` VALUES ('1401', '140', '单刀赴会', '8', '0', '关羽旋转大刀队附近的敌人造成215/430/645/860/1075/1290（+127％物理加成）点物理伤害；冲锋姿态：关羽向前发起冲锋造成（100％物理加成）（10％最大生命值）点物理伤害，并在结束时向前劈砍，造成250/500/750/1000/1250/1500（+150％物理加成）点物理伤害', '非冲锋状态下是AOE范围伤害，团战中关羽尽量在混战中心多次使用此技能，打到更多的人，才能发挥更大的价值；冲锋状态下配合普攻有一个不错的突进效果和小爆发伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/140/14010.png');",
                "INSERT INTO `skill` VALUES ('1402', '140', '青龙偃月', '14', '0', '关羽清啸一声，解除自身控制效果并增加30％移动速度，持续2秒；冲锋姿态：关羽清啸一声，解除自身控制效果并向前跃击敌人将其击退，造成335/470/605/740/875/1010(+112％物理加成）点物理伤害', '移速加成效果无疑使得关羽更加灵活，由于可以解除控制效果，这就成为了关羽绝佳的逃跑技能，所以需要注意要在敌方交了控制技能后再使用此技能，在安全的情况下，可以利用此技能进行突进留人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/140/14020.png');",
                "INSERT INTO `skill` VALUES ('1403', '140', '刀锋铁骑', '70', '0', '关羽激发潜能，将减少50％的冲锋准备距离，持续10秒；冲锋姿态：关羽将召唤铁骑向前方突击撞退敌人，并造成（+100％物理加成）（+10％最大生命值）点物理伤害', '关羽能快速进入冲锋状态，冲锋状态下能击退大范围的敌人，对于保护队友和突进先手都是非常实用的', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/140/14030.png');",
                "INSERT INTO `skill` VALUES ('1230', '123', '饕餮血统', '0', '0', '被动：吕布拥有比其他近战更远的攻击距离；方天画戟附魔状态下，普通攻击会造成100%物理加成的真实伤害并且回复自身已损生命值1%（+25%额外物理加成）点生命值；如果下一次方天画斩未命中英雄，会立刻失去附魔效果', '吕布拥有比普通战士更远的普攻距离，附魔状态下，有更高的伤害和血量回复，保证了吕布的续航能力，也能更好的进行发育', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/123/12300.png');",
                "INSERT INTO `skill` VALUES ('1231', '123', '方天画斩', '5', '0', '吕布向指定方向挥动方天画戟，造成600/720/840/960/1080/1200（+225%额外物理加成）点物理伤害，如果命中英雄，将会附魔自身武器，武器附魔状态下，方天画斩会造成600/720/840/960/1080/1200（+225%额外物理加成）点真实伤害，并且每命中一位敌方英雄会回复自身已损生命4%（+50%额外物理加成）点生命值', '在命中敌方英雄后，普攻能造成真实伤害和血量回复，是吕布在团战中持续输出的重要保证', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/123/12310.png');",
                "INSERT INTO `skill` VALUES ('1232', '123', '贪狼之握', '12', '0', '吕布夺取指定方向范围内敌人的魂灵，对其造成80（+35%额外物理加成）点物理伤害并减少其30%移动速度，持续3秒，每夺取一个魂灵都会为自身增加可抵免400/550/700/850/1000/1150点伤害的护盾，最多叠加3层。吕布的护盾会随时间转化为血量，每秒转化当前护盾的25%，每2点护盾转化为1点血量。', '在命中更多敌方单位后护盾效果更强，也能进行轻微的减速留人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/123/12320.png');",
                "INSERT INTO `skill` VALUES ('1233', '123', '魔神降世', '60', '0', '吕布在指定位置从天而降展开杀戮，落地时会标记范围并将范围内敌人击飞1秒，如果命中英雄，将会附魔自身武器，未命中英雄不会丢失原有的附魔状态；在标记范围内施展方天画斩会造成400/650/900（+250%额外物理加成）点物理伤害，并且方天画斩攻击范围会增加；触碰范围边缘的敌人会受到150（+87%额外物理加成）点物理伤害并减少其50%移动速度，持续2秒；当敌方英雄触碰时还会额外回复吕布自身已损生命4%（+50%额外物理加成）点生命值', '非常强大的控制技能，范围非常大，能够击飞范围内所有敌人，非常适合开团使用.当然，在面对敌方gank时，也可以用来当作位移技能逃跑', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/123/12330.png');",
                "INSERT INTO `skill` VALUES ('1270', '127', '凝泪成冰', '0', '0', '被动：甄姬的每次技能伤害都会为目标叠加冰冻印记，持续7秒，叠加满3层后目标将被冰冻1秒，造成350（+52％法术加成）点法术伤害', '甄姬通过被动技能的存在来打出控制效果和更多的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/127/12700.png');",
                "INSERT INTO `skill` VALUES ('1271', '127', '泪如泉涌', '8', '70', '甄姬在指定位置召唤水柱冲出地面，对范围内敌人造成500/590/680/770/860/950（+70％法术加成）点法术伤害并击飞1秒', '甄姬的控制技能，技能有一定的延迟时间，作为先手效果很差，不容易控制对手，需要预判对手的走位施放', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/127/12710.png');",
                "INSERT INTO `skill` VALUES ('1272', '127', '叹息水流', '6', '60', '甄姬引导指定方向的水流，水流会锁定敌人并在敌人间弹射，最多弹射3次，每次弹射会对敌人造成300/350/400/450/500/550（+45％法术加成）点法术伤害', '这是一个不错的AOE技能，伤害也很高，而且是选中单体目标，非常稳定，团战中能造成非常可观的输出，CD也比较短，前期主要用作清线和消耗使用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/127/12720.png');",
                "INSERT INTO `skill` VALUES ('1273', '127', '洛神降临', '50', '100', '甄姬朝指定方向释放水之精魄不断向前推进并击退非英雄单位，当水之精魄命中敌方英雄或者达到最远距离时将形成水域，第一个直接被命中的英雄受到400/500/600（+65％法术加成）点法术伤害并减少90%移速持续1秒；水之精魄形成的水域持续5秒，每秒对触碰的敌人造成200/250/300（+32％法术加成）点法术伤害并减少其50％移动速度，持续2秒', '五杀神技！在范围内持续造成高额伤害，而且可以配合被动形成AOE控制技能', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/127/12730.png');",
                "INSERT INTO `skill` VALUES ('1480', '148', '心魔', '0', '0', '被动：姜子牙每10秒为友军增加16点经验', '功能型被动技能，可以使友方英雄等级领先于敌方，前期在3级到4级的时间点尤为重要', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/148/14800.png');",
                "INSERT INTO `skill` VALUES ('1481', '148', '忏悔', '8', '70', '姜子牙聚集能量对目标区域的敌人进行冲击，对受到冲击的敌人造成每0.5秒75/87/99/111/123/135（+7%法术加成）点法术伤害，持续5秒；持续期间敌人还将逐渐减少移动速度，物理和法术防御，最多将会减少90%移动速度，25%/28%/31%/34%/37%/40%物理防御和法术防御', 'AOE范围技能，有较高的AP伤害加成和可叠加的减速效果，在人群中使用效果极佳', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/148/14810.png');",
                "INSERT INTO `skill` VALUES ('1482', '148', '湮灭', '10', '80', '姜子牙在指定位置引爆能量，对范围内敌人造成310/350/390/430/470/510（+47%法术加成）点法术伤害并将其击退，同时将会减少50%移动速度，持续0.5秒', '范围内造成法术伤害并能击退敌人，是姜子牙重要的保命技能', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/148/14820.png');",
                "INSERT INTO `skill` VALUES ('1483', '148', '断罪', '10', '100', '姜子牙朝指定方向引导能量聚集，最多持续3秒，冲击波会造成450/600/750（+35%法术加成）～1350（+105%法术加成）点法术伤害并且可对路径上的建筑造成10%伤害；聚集时间会影响冲击波的伤害和攻击距离，冲击波最远可攻击1800距离内的敌人；进行任意操作都会中断聚集过程并立即放出冲击波', '姜子牙最主要的输出和消耗技能，施法范围非常大，距离和伤害根据蓄力的时间而定，但施法时间较长，需要有非常好的预判能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/148/14830.png');",
                "INSERT INTO `skill` VALUES ('1240', '124', '引燃', '0', '0', '被动：周瑜普通攻击和技能的火焰会对敌人和敌方建筑叠加引燃印记，持续4秒；叠加满4层后目标将被引燃，在4秒内每秒承受125（+17%法术加成）点法术伤害并减少其30%移动速度，持续1秒；引燃期间目标不会再被叠加印记', '周瑜通过被动可以快速推线拿塔，并且让处于火区的敌人停留更长的时间', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/124/12400.png');",
                "INSERT INTO `skill` VALUES ('1241', '124', '东风浩荡', '6', '60', '周瑜向指定方向召唤狂风形成一个单向风区，风区内己方英雄移速提升10%，敌方英雄移速降低20%，持续5秒，狂风吹起瞬间，还会将附近的敌人朝风向方向击退并造成200/240/280/320/360/400（+30%法术加成）点法术伤害，火区若被风区持续覆盖1.5秒，会朝风向方向蔓延出新的火区，火区每0.5秒对敌人造成一次125/150/175/200/225/250（+17%法术加成）点法术伤害', '风向决定了火区扩散的方向，需要准确的判断战场的位置以及随后的发展趋势', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/124/12410.png');",
                "INSERT INTO `skill` VALUES ('1242', '124', '流火之矢', '0', '50', '周瑜下令发射火矢轰击指定目标区域，火矢轰击会造成250/300/350/400/450/500（+34%法术加成）点法术伤害并留下一块火区，持续8秒，每0.5秒对火区内敌人造成一次125/150/175/200/225/250（+17%法术加成）点法术伤害。火区的数量若超过5个将清除较早生成的火区。火矢每10秒可储备1次，最多储备3次', '火区重叠的部分持续伤害不会叠加，所以需要让火区合理的分散分布于战场', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/124/12420.png');",
                "INSERT INTO `skill` VALUES ('1243', '124', '烽火赤壁', '10', '100', '周瑜向指定范围方向召唤一片火焰，造成200/300/400（+40%法术加成）点法术伤害，火焰掠过火区会激起一道火风，火风会造成额外200/300/400（+40%法术加成）点法术伤害并将敌人晕眩1秒', '通过扩散开的火区可以发起大范围的控制和制造大量伤害，并且让处于控制中的敌人停留于火区受到更多的持续伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/124/12430.png');",
                "INSERT INTO `skill` VALUES ('1200', '120', '反击之镰', '0', '0', '被动：白起的普通攻击和技能将会减少目标对白起造成5％~12％的伤害，持续3秒，减少幅度随英雄等级成长', '白起通过堆肉装能更有效的利用被技能，让白起在团战中更加坚挺，一战到底.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/120/12000.png');",
                "INSERT INTO `skill` VALUES ('1201', '120', '血之回响', '7', '40', '白起回旋大镰血洗四方，对范围内的敌人造成150/200/250/300/350/400（+40％物理加成）（+3％最大生命值）点物理伤害，并回复2％已损失生命值，命中敌方英雄时会额外回复2％已损生命；被动：白起受伤时有20％几率释放血之回响', '白起唯一的输出技能，伤害不高，但CD较短，可以反复使用此技能进行消耗', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/120/12010.png');",
                "INSERT INTO `skill` VALUES ('1202', '120', '死神之镰', '14', '60', '白起在指定位置召唤镰刀，镰刀在一定延迟后收回，对范围内的敌人造成200/230/260/290/320/350（+100％物理加成）点法术伤害并将其拉回；同时敌人会减少50％移动速度，持续2.5秒；被动：白起对生命值低于30％的目标会额外造成30％伤害', '群体控制技能，但缺点是距离短，出手时间长，先手很容易被躲开，但作为后手时，会较容易控制到敌方多人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/120/12020.png');",
                "INSERT INTO `skill` VALUES ('1203', '120', '傲慢嘲讽', '40', '100', '白起向指定位置跳跃，造成300/380/460（+100％物理加成）点法术伤害并嘲讽敌人，持续1/1.3/1.5秒（随技能等级成长）；嘲讽时间会随着白起的额外生命值增加，每1000点额外生命值额外提供0.2秒嘲讽时长，最多持续2.5秒；期间受到生命回复效果提升100％；被动：白起受到攻击时会获得庇佑，增加10点移动速度与30/60/90点物理防御，持续3秒，最多叠加3层', '控制技能，团战时冲到人群尽量嘲讽更多敌人形成控制效果，为友军创造输出条件', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/120/12030.png');",
                "INSERT INTO `skill` VALUES ('1670', '167', '大圣神威', '0', '0', '被动：孙悟空每次释放技能后下一次普通攻击变更为强力敲击，强力敲击会冲锋至目标身旁并抡起金箍棒敲打造成370（+100%物理加成）点物理伤害，该伤害可全额享受暴击收益。孙悟空天生拥有20%暴击几率，但初始只能造成150%暴击伤害', '孙悟空需要利用技能接普攻打出更高的伤害，被动的暴击加上暴击装备的加成能迅速击杀对方脆皮英雄', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/167/16700.png');",
                "INSERT INTO `skill` VALUES ('1671', '167', '护身咒法', '12', '70', '孙悟空念起护身咒，护身咒可为孙悟空抵挡一次敌方技能，护身咒最多持续1.5秒；若成功抵挡，孙悟空将获得0.2秒的无敌效果以及抵免500/600/700/800/900/1000（+150%物理加成）点伤害的护盾，持续4秒。开启技能时自身增加40%移动速度，持续1秒', '在适当的时间开启，成功抵挡敌方技能后，能让孙悟空抵挡大量的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/167/16710.png');",
                "INSERT INTO `skill` VALUES ('1672', '167', '斗战冲锋', '8', '50', '孙悟空向指定方向冲锋，如果冲锋路径上遭遇敌人会借力腾空跳跃；释放普通攻击或技能可中断跳跃', '这是孙悟空很重要的位移技能，主要利用它来接近敌人或借力位移追击其他的敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/167/16720.png');",
                "INSERT INTO `skill` VALUES ('1673', '167', '如意金箍', '40', '100', '孙悟空将变大的金箍棒直插入地，对范围内敌人造成150/190/230（+50%物理加成）点物理伤害并将其晕眩1秒。同时范围内敌人获得3层印记；孙悟空的普通攻击命中带有印记的敌人将消耗1层印记并额外造成270/405/540点物理伤害。', '这是孙悟空主要的控制技能，能形成很好的AOE控制效果，并且对印记标记的敌人有更高的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/167/16730.png');",
                "INSERT INTO `skill` VALUES ('1920', '192', '炮手燃魂', '0', '0', '被动：黄忠普攻命中目标将不断激怒自己，获得1.5%暴击率和1%攻击力加成的效果（重装炮塔状态下效果翻倍），持续1.5秒，最多叠加5层。', '黄忠在进行持续普攻输出时，被动增加自己暴击和攻击力加成，在被动存在时能造成更多输出。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/192/19200.png');",
                "INSERT INTO `skill` VALUES ('1921', '192', '追击潜能', '10', '50', '黄忠短时间大量提升自身70%移速，移速会在1.5秒内衰减，如果是在架起状态，可瞬间取消架起状态。被动：增加黄忠40/52/64/76/88/100点物理攻击。', '黄忠可以利用1技能加速逃跑或追击敌人，同时增加自身攻击力，在大招架起状态下可快速取消。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/192/19210.png');",
                "INSERT INTO `skill` VALUES ('1922', '192', '警戒地雷', '8', '80', '黄忠向指定地点放置警戒地雷，同时获得视野效果，敌方在地雷范围内可触发造成200/240/280/320/360/400（+60%物理加成）点物理伤害，对英雄额外造成50%减速效果与15/18/21/24/27/30%的破甲效果，持续2秒，同时自身获得护盾，可吸收200/240/280/320/360/400（+50%物理加成）点伤害。地雷最多存在2个。', '黄忠在进行对拼时可以利用技能形成吸收伤害的护盾，利于生存和减速敌人；可以在草里放置地雷以查看视野。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/192/19220.png');",
                "INSERT INTO `skill` VALUES ('1923', '192', '重装炮台', '16', '80', '黄忠进入架起状态，自身不能移动，但会增加180/270/360点物理防御和法术防御，持续15秒，再次释放后退出架起状态；架起状态下，黄忠的每次普通攻击会对中心范围内的敌人造成350/525/700（+100%物理加成）点物理伤害（对边缘敌人伤害衰减30%）；黄忠有7次攻击机会，使用完后取消架起状态', '这是玩好黄忠的关键技能，根据场上局势黄忠需要选取一个很好的架炮位置进行输出，随着架炮时间增加，攻击距离和伤害增加。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/192/19230.png');",
                "INSERT INTO `skill` VALUES ('1620', '162', '流刀舞术', '0', '0', '被动：娜可露露的技能命中敌人时，下一次普通攻击变更为强化攻击，强化攻击会额外造成80（+100%额外物理加成）的物理伤害。', '大幅度提升娜可露露普攻的输出能力，因为效果不可叠加，所以需要控制技能的施放节奏，而且要保证技能的命中率', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/162/16200.png');",
                "INSERT INTO `skill` VALUES ('1621', '162', '飞鹰攻击', '10', '70', '娜可露露召唤玛玛哈哈向指定方向攻击敌人，造成350/400/450/500/550/600（+155%额外物理加成）点物理伤害，并对敌人施加鹰眼标记；娜可露露的其它伤害可以触发印记，对敌人造成最大生命值8%物理伤害；每40点额外物理攻击额外增幅1%；触发印记飞鹰攻击会减少3秒冷却时间并恢复35点法力值', '尽量用此技能先手标记，然后利用其他伤害来触发标记，对敌人造成高额的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/162/16210.png');",
                "INSERT INTO `skill` VALUES ('1622', '162', '风之刃', '6', '50', '娜可露露向指定方向冲刺，对路径上的敌人造成350/420/490/560/630/700（+170%额外物理加成）点物理伤害，每命中一名敌方英雄将回复250/300/350/400/450/500（+100%额外物理加成）点生命值.', '尽量命中足够多的英雄来回复自己血量', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/162/16220.png');",
                "INSERT INTO `skill` VALUES ('1623', '162', '飞鹰急袭', '12', '120', '娜可露露召唤玛玛哈哈并肩作战，立即增加50%的移动速度，持续6秒，该效果会持续衰减；再次释放飞鹰急袭，娜可露露将朝指定区域发起进攻，对敌人造成600/850/1100（+265%额外物理加成）点物理伤害并减少50%移动速度，持续3秒；受到冲击的敌人将减少30%伤害输出，持续3秒', '进可攻退可守的技能，不但机动性强，而且伤害可观，需要注意在飞行阶段，不能施放普攻或其它技能', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/162/16230.png');",
                "INSERT INTO `skill` VALUES ('1120', '112', '火力压制', '0', '0', '被动：鲁班七号连续使用普通攻击时，第五次普通攻击会掏出机关枪进行扫射，扫射会造成3次伤害，对敌人英雄每次造成其最大生命5%物理伤害（每80点额外物理攻击提升1%），对小兵野怪防御塔造成120（+50%物理加成）点物理伤害', '有被动的鲁班是相当强势的，对线时尽量叠被动那一下去消耗敌方远程英雄', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/112/11200.png');",
                "INSERT INTO `skill` VALUES ('1121', '112', '河豚手雷', '7', '50', '鲁班向指定位置投掷一枚河豚手雷，对范围内的敌人造成300/350/400/450/500/550（+100%物理加成）点物理伤害并减少其25%移动速度，持续2秒;使用手雷后鲁班七号下一次普通攻击更变为扫射', '能造成短时间减速和眩晕效果，团战时尽可能命中更多敌人，利用普攻配合技能持续输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/112/11210.png');",
                "INSERT INTO `skill` VALUES ('1122', '112', '无敌鲨嘴炮', '15', '70', '鲁班向指定方向发射火箭炮击退身前敌人，命中英雄后造成260/295/330/365/400/435（+100%物理加成）点物理伤害，并附带目标已损生命5/6/7/8/9/10%法术伤害，发射火箭炮后鲁班七号下一次普通攻击变更为扫射', '全屏技能，伤害不高，可以在对线时进行消耗，可以在将敌人致残后进行远程收割', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/112/11220.png');",
                "INSERT INTO `skill` VALUES ('1123', '112', '空中支援', '40', '100', '鲁班召唤河豚飞艇向指定方向进行空中支援，支援持续14秒，河豚飞艇可照亮视野且每秒对范围内随机一个敌人投掷炸弹，炸弹会在0.75秒落下，对于目标范围内的敌人造成320/400/480（+123%物理加成）点物理伤害；召唤飞艇后鲁班七号下一次普通攻击变更为扫射', '技能速度很慢，范围极大，能起到一定的威慑作用，团战可打出可观的AOE伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/112/11230.png');",
                "INSERT INTO `skill` VALUES ('1090', '109', '失心', '0', '0', '被动：如果妲己技能命中敌人，将会减少目标30~72点法术防御，最多叠加3层，减少幅度随英雄等级成长', '妲己能输出高额法术伤害的保证', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/109/10900.png');",
                "INSERT INTO `skill` VALUES ('1091', '109', '灵魂冲击', '8', '80', '妲己向指定方向挥出灵魂冲击波，对命中的敌人造成520/585/650/715/780/845（+122%法术加成）点法术伤害', '妲己的主要伤害与消耗技能，攻击距离较长，有强大的消耗能力.但技能出手较慢，有一定的施法延迟', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/109/10910.png');",
                "INSERT INTO `skill` VALUES ('1092', '109', '偶像魅力', '12', '90', '妲己锁定敌人并抛出魅力爱心，对命中的敌人造成285/320/355/390/425/460（+66%法术加成）点法术伤害，并将其眩晕1.5秒', '妲己的控制技能，缺点是施法范围较小，所以务必保证敌人在施法范围内时再释放技能控制敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/109/10920.png');",
                "INSERT INTO `skill` VALUES ('1093', '109', '女王崇拜', '18', '120', '妲己放出5团狐火自动攻击附近的敌人，每团狐火造成325/405/485（+75%法术加成）点法术伤害，当多团狐火命中同一个目标时，从第二团狐火开始将只造成50%伤害', '配合控制技能可瞬间将敌人打成残血，后期在法伤足够条件下，可瞬间秒杀敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/109/10930.png');",
                "INSERT INTO `skill` VALUES ('1900', '190', '策谋之刻', '0', '0', '被动：诸葛亮法术命中敌人时，会为自己施加谋略刻印，当刻印达到5层时，会召唤5颗法球围绕自身，短暂时间后依次飞出攻击附近的敌人造成270(+52%法术加成)点法术伤害；法球在1秒内连续命中同一目标时，从第二颗法球开始将只造成20%伤害；法球会优先攻击敌方英雄，法球不会攻击非战斗状态的野怪', '诸葛亮可充分利用技能的释放叠加被动，5层时对敌人造成一波爆发性伤害，同时被动技能对刷野的的效率也大大提高.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/190/19000.png');",
                "INSERT INTO `skill` VALUES ('1901', '190', '东风破袭', '8', '60', '诸葛亮指定方向发射三颗法球，对路径上的敌人造成500/560/620/680/740/800（+75%法术加成）点法术伤害，如果多颗法球命中同一目标时，从第二颗法球开始将只造成30%伤害；每颗法球能触发1次谋略刻印', '把握东风破袭的释放距离，打出更高的伤害并增加更多的谋略刻印.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/190/19010.png');",
                "INSERT INTO `skill` VALUES ('1902', '190', '时空穿梭', '2', '50', '诸葛亮快速朝指定方向闪烁，并在起始位置和终点位置同时产生一次法力场，对范围内敌人造成350/420/490/560/630/700（+52%法术加成）点法术伤害；同一目标如果在5秒内多次受到法力场影响，将会减少其90%移动速度，持续3秒，该效果会持续衰减；当目标被多个法力场同时命中，将只造成50%伤害；每次法力场影响到敌人，都会触发1次谋略刻印；每10秒可准备1次时空穿梭，最多可储备3次时空穿梭；', '该技能属于诸葛亮的核心技能，也是诸葛亮唯一的位移技能.对于释放的把控时机可在被抓时使用，也可利用该技能在团战时骗对方突进位移.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/190/19020.png');",
                "INSERT INTO `skill` VALUES ('1903', '190', '元气弹', '35', '120', '诸葛亮短暂蓄力后锁定目标发出一击强力元气弹，造成450/600/750（+50%法术加成）点法术伤害；目标每损失1%最大生命值元气弹就会增加2%伤害；蓄力时诸葛亮可以移动，并且可以使用时空穿梭；元气弹飞行过程中碰撞到敌方非英雄目标，将会触发同等伤害并将其击飞0.5秒；元气弹可触发1次谋略刻印；如果元气弹直接击败敌方英雄，立即发动策谋之刻，并减少80%元气弹的冷却时间', '诸葛亮的大招可以被敌人队友挡住伤害，尽量在对手一个人的时候施放.选择利用大招收割人头的时机可使技能冷却时间刷新更快.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/190/19030.png');",
                "INSERT INTO `skill` VALUES ('1910', '191', '川流不息', '0', '0', '被动：大乔与附近600范围内最近的队友将会增加40～60移动速度，增加幅度随英雄等级成长', '移速提升对大乔自己来说有很强的支援能力，对于协助队友追人和逃跑也非常实用', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/191/19100.png');",
                "INSERT INTO `skill` VALUES ('1911', '191', '鲤跃之潮', '7', '70', '大乔召唤鲤鱼向指定方向跳跃，对于路径上的敌人造成550/700/850/1000（+56%法术加成）点法术伤害并将其击退；鲤鱼会形成河流，持续4秒，友军经过时会增加30%移动速度', '这个技能可以让大乔很好的保护友方英雄进行撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/191/19110.png');",
                "INSERT INTO `skill` VALUES ('1912', '191', '宿命之海', '8', '50', '大乔在指定位置召唤法阵，持续4秒，4秒后会将法阵内所有英雄传送回出生点；同时回复其全部生命并增加25%移动速度，持续3秒', '这个技能使得大乔可以让残血队友快速回家，并且可以与大招联动使用，可能会有有出其不意的效果哦', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/191/19120.png');",
                "INSERT INTO `skill` VALUES ('1913', '191', '决断之桥', '11', '80', '大乔在指定位置召唤海潮，每次0.5秒造成180/210/240/270（+20%法术加成）点法术伤害，持续4秒；海潮范围内，敌人将被沉默', '这个技能施放位置选好了会使得对方非常头疼，对区域敌人造成持续的沉默效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/191/19130.png');",
                "INSERT INTO `skill` VALUES ('1914', '191', '漩涡之门', '70', '120', '大乔在指定位置召唤大型法阵，对范围内敌人造成660/830/1000（+72%法术加成）点法术伤害；法阵将持续8秒；法阵存在时，所有队友可以通过点击旋窝之门传送至法阵位置；当己方英雄处于法阵范围内时，会增加40%攻击速度', '这个技能是大乔有很高战术意义的技能，可以快速集结队友对敌方进行意想不到的狙击行动', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/191/19140.png');",
                "INSERT INTO `skill` VALUES ('1560', '156', '言灵·咒令', '0', '0', '被动：张良对任一敌人造成的相邻两次普攻或技能伤害的时间间隔若小于1.5秒，这两次伤害的间隔时间被视为“连续攻击状态”，该状态每积累满1.2秒，会使该敌人额外承受140（+50%法术加成）点真实伤害，该伤害随英雄等级每级成长10点', '对任一敌人保持一定的攻击频率不中断，便可以打出额外伤害，“拖住敌人”是核心目的', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/156/15600.png');",
                "INSERT INTO `skill` VALUES ('1561', '156', '言灵·壁垒', '11', '80', '张良以言灵之术召唤4道并列的法术壁垒，敌人触碰壁垒将受到400/450/500/550/600/650（+75%法术加成）点法术伤害、0.75秒晕眩和之后0.5秒50%减速，并撞碎这块壁垒。重复触碰壁垒的敌人受到的伤害衰减50%，不再承受晕眩效果，但会承受1.25秒50%减速效果', '前摇很短的远程硬控技能，主要用来后手阻断敌人移动，对留人和自己逃生都有不错效果。操作熟练的话，用来直接命中敌人，也可以作为一个不错先手技能', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/156/15610.png');",
                "INSERT INTO `skill` VALUES ('1562', '156', '言灵·命数', '2', '80', '张良以言灵之术召唤侵蚀法阵，法阵每0.5秒对踏入其中的敌人之一造成120/144/168/192/216/240（+17%法术加成）点法术伤害，同时最多存在2个法阵(法阵对小兵野怪额外造成50%伤害)。法阵每12秒存储一次（受到减CD属性影响），最多存储3次，每个法阵持续5.5秒。张良在法阵中提升60点移动速度。法阵可提供视野。', '技能本身不带控制，范围较大，爆发弱，持续输出高。因此释放时需要预判敌人走位，并利用其它技能尽量将敌人留在该技能输出范围内。利用攒点施法的机制，可以连续放置两次法术，覆盖大范围，避免敌人逃出攻击范围', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/156/15620.png');",
                "INSERT INTO `skill` VALUES ('1563', '156', '言灵·操纵', '40', '130', '张良以言灵之术抓住一名敌方英雄，持续2秒，期间每0.5秒造成120/150/180（+15%法术加成）点法术伤害，并持续支配对方。技能完整释放后，还会在目标身下生成一个法术区域，对踏进其中的敌人造成240/300/360（+30%法术加成）点法术伤害和0.75秒晕眩', '伤害不高，但如果在已经磨掉对方一定血量之后，该技能长时间的控制配合2技能伤害往往能一套带走对方。与高伤害队友配合时，击杀概率会大大增加', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/156/15630.png');",
                "INSERT INTO `skill` VALUES ('1390', '139', '师道尊严', '0', '0', '被动：老夫子普通攻击命中会增加1点训诫值，最多叠加5点，叠满后会获得强化自身，持续5秒；强化时老夫子会增加60点移动速度和25%攻击速度，同时普通攻击将会附带60点真实伤害，每次攻击能够减少1秒圣人训诫和举一反三的冷却时间', '可以通过普攻小兵或野怪积攒被动强化效果，攒满后对敌方英雄进行攻击，此状态下老夫子战斗力爆表', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/139/13900.png');",
                "INSERT INTO `skill` VALUES ('1391', '139', '圣人训诫', '8', '0', '老夫子向指定方向挥动戒尺，将范围内的敌人抽到自己身前，造成200/240/280/320/360/400（+80%物理加成）点物理伤害并减少其25%移动速度，持续1秒', '这是老夫子主要控制留人技能，施法时要注意与敌人的距离和施法方向', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/139/13910.png');",
                "INSERT INTO `skill` VALUES ('1392', '139', '举一反三', '10', '0', '老夫子爆发气场，增加25/28/31/34/37/40%移动速度并且减少50/54/58/62/66/70%受到的伤害，持续2秒，同时会反弹英雄的普通攻击，对攻击者造成等量的物理伤害并减少其50%移动速度，持续2秒；反弹会附带普通攻击的法球效果，每个敌人最多被反弹一次；被动：每次普通攻击有25%几率发动两次普通攻击，第二次普通攻击只会造成50%伤害', '这是老夫子在刚正面时非常重要的一个技能，在和敌人对拼时适当的时机施放会是的老夫子的战斗力更强', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/139/13920.png');",
                "INSERT INTO `skill` VALUES ('1393', '139', '圣人之威', '45', '0', '老夫子向指定方向冲锋造成400/500/600（+120%物理加成）点物理伤害，并且会将第一个撞到的敌方英雄捆在明灯上，令其无法离开明灯的范围，持续5秒，期间将减少20%该英雄造成的伤害', '对于被老夫子抓住的敌人来说，这个技能简直是脆皮英雄的噩梦，超强的控制效果能够帮助他和队友更好的击杀敌人，这个技能由于是非指向性的，所以施放时需要准确预判敌人走位', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/139/13930.png');",
                "INSERT INTO `skill` VALUES ('1130', '113', '自然意志', '0', '0', '被动：庄周每隔6秒进入自然梦境，会解除自身所有控制效果，并且减少15%所受到的伤害，增加15%移动速度，持续2秒', '被动效果无疑提升了庄周的生存能力', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/113/11300.png');",
                "INSERT INTO `skill` VALUES ('1131', '113', '化蝶', '8', '80', '庄周幻化蝴蝶飞向指定方向，对路径上的敌人造成375/450/525/600/675/750（+69%法术加成）点法术伤害，并减少其50%移动速度，持续2秒', '此技能配合被动效果能在追击敌人时留人，能在面对追击时减速多个敌人安全撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/113/11310.png');",
                "INSERT INTO `skill` VALUES ('1132', '113', '蝴蝶效应', '6', '40', '庄周梦中蝴蝶扇起翅膀，对身边的敌人造成250/320/390/460/530/600（+30%法术加成）的法术伤害，并增加范围内友军3%移动速度，持续5秒，技能连续命中会叠加效果，伤害每层叠加125/160/195/230/265/300（+15%法术加成）的法术伤害，移动速度每层叠加3%，最多叠加4层；被动：庄周每隔6秒会自动释放一次蝴蝶效应', '该技能CD较短，可多次使用，不管是追击还是撤退都是好的选择', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/113/11320.png');",
                "INSERT INTO `skill` VALUES ('1133', '113', '天人合一', '60', '150', '庄周幻化梦域保护附近友军，使得他们免疫控制效果并且减少10%所受到的伤害，持续2秒', '一个非常强大的保护技能，此技能效果为友军减伤并提高伤害，配合装备军团荣耀效果极佳', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/113/11330.png');",
                "INSERT INTO `skill` VALUES ('1840', '184', '长歌行', '0', '0', '被动：当蔡文姬受到伤害时，自身会立刻增加70%持续衰减的移动速度，持续2秒，同时自身会每秒回复250（+50%法术加成）点生命值，持续2秒；长歌行每10秒只能触发一次', '蔡文姬在逃跑受到伤害的同时，对敌方的任何攻击都会触发被动效果，减速敌人，能更好地进行撤退，小兵和野怪的伤害也能触发这个技能效果，可以利用此效果进行支援gank留人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/184/18400.png');",
                "INSERT INTO `skill` VALUES ('1841', '184', '思无邪', '15', '100', '蔡文姬演奏乐曲，自身会增加20%移动速度，持续3秒，同时将为自身和周围的友方英雄每0.5秒恢复60/66/72/78/84/90（+20%法术加成）点生命值，持续3秒', '在技能范围内的友军能持续回复生命值，并提升移动速度和攻击速度，所以技能施放时尽量靠近多个友军，使得收益最大化', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/184/18410.png');",
                "INSERT INTO `skill` VALUES ('1842', '184', '胡笳乐', '9', '70', '蔡文姬向指定方向弹奏一束音波，命中后会在敌人间弹射，每次弹射造成250/290/330/370/410/450（+36%法术加成）点法术伤害并将其眩晕0.75秒；每束音波最多弹射6次，同一目标最多受到2次弹射效果，第二次弹射命中时将只造成50%的初始伤害', '这个技能在敌方英雄聚集的时候能发挥巨大的效果，能起到很大的控制作用，在面对追击反手释放时，命中率更高', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/184/18420.png');",
                "INSERT INTO `skill` VALUES ('1843', '184', '忘忧曲', '60', '120', '蔡文姬释放琴音围绕四周，每0.5秒为范围内血量最低的友方英雄回复100/150/200（+60%法术加成）点生命值，持续5秒，同时为其增加300/375/450（+25%法术加成）点物理和法术防御', '团战时，尽量使技能范围包含更多的友军，能降低他们受到的伤害，敌人在大招范围内超过2秒能清空他们的护甲和魔抗，所以对于敌方的坦克英雄来说，这个技能会使他们相当头疼', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/184/18430.png');",
                "INSERT INTO `skill` VALUES ('1710', '171', '黑暗潜能', '0', '0', '被动：张飞变身前，每次普通攻击会增加狂意；张飞变身后，施放技能会使得周围的地面震动，附近的敌人将会减少50％移动速度，持续2秒；并且变身后的普通攻击将会造成40（+200％物理加成）点伤害', '张飞的被动技能可以说是神技，变身的张飞就是一个强力坦克输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/171/17100.png');",
                "INSERT INTO `skill` VALUES ('1711', '171', '画地为牢', '6', '0', '张飞向四周挥扫蛇矛，对范围内的敌人造成450/550/650/750/850/950（+100％物理加成）点物理伤害；命中敌方英雄时会将其击退；每命中一个敌方非英雄单位会增加1点狂意，命中敌方英雄会增加3点狂意；变身后，张飞会向指定方向疯狂的砸击地面，对范围内的敌人造成450/550/650/750/850/950（+100％物理加成）点物理伤害', '技能的伤害在变身前不是很高，更多的是我们需要他的击退效果，击退对面切入的刺客，保护自己的后排，或者打野、打小兵来快速积攒张飞的能量', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/171/17110.png');",
                "INSERT INTO `skill` VALUES ('1712', '171', '守护机关', '10', '0', '张飞向指定区域跳跃，落地时启动机关印记，机关会给范围内友军生成可抵免500/675/850/1025/1200/1375（+47％物理加成）点伤害的护盾；释放技能会增加3点狂意，每给一位友方英雄生成一个护盾可额外增加1点狂意；变身后，张飞向指定区域跳跃，践踏该区域敌军造成380/430/480/530/580/630（+94％物理加成）点物理伤害', '这个技能在张飞在变身前是辅助技能，变身前没有伤害，可以在打团的时候给队友开着来减伤，也可以让张飞逃跑，当然变身后，又是一个很给力的输出技能，变大，2技能切入战场，是一个张飞很有效的组合！', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/171/17120.png');",
                "INSERT INTO `skill` VALUES ('1713', '171', '狂兽血性', '40', '100', '张飞狂意达顶峰，指定方向怒吼形成狂风通路并展现魔种真身，获得相当于自身最大生命值40％的护盾，持续15秒；怒吼期间免疫控制，狂风会推开通路两边敌人，同时对通路末端敌人造成500/750/1000（65％物理加成）点物理伤害并将其眩晕1.5秒；张飞在通路范围内会增加50％移动速度', '在张飞进行变身之后，这个技能不仅能够增加自己一个护盾，而且还能够对自己正前方的道路释放一定的技能，对道路两边的敌人造成伤害的同时还会造成一定的眩晕效果.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/171/17130.png');",
                "INSERT INTO `skill` VALUES ('1960', '196', '瞄准', '0', '0', '被动：百里守约的普通攻击会造成180%物理加成点物理伤害，但攻击间隔更长且无法暴击；百里守约每获得1%暴击几率就会将其转化为3点物理攻击力；非战斗状态下，百里守约能够隐匿在地形边缘，进入伪装状态，并增加16%~30%移动速度，移动速度随英雄等级成长', '百里守约只需要追求更高的物理攻击和物理穿透; 而伪装则能帮助百里守约寻找安全的狙击位置或者追杀中轻松逃脱', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/196/19600.png');",
                "INSERT INTO `skill` VALUES ('1961', '196', '静谧之眼', '1', '30', '百里守约在脚下布置一个视野装置，获得600~1000范围的全部视野，随技能等级成长；视野装置每50/48/46/44/42/40秒可准备1个，最多储备3个；同时最多放置3个视野装置，每个视野装置可持续300秒；视野装置被敌方英雄占领后失效；百里守约普通攻击和技能对探测视野内的野怪会额外造成伤害；被动：原地不动时百里守约每秒获得1层伏击效果，每层伏击效果会增加7/8/9/10/11/12%物理穿透，最多叠加5层；移动后伏击效果消失', '通过静谧之眼掌控视野，把控全局，技能的被动会让百里守约狙击敌人时造成更高的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/196/19610.png');",
                "INSERT INTO `skill` VALUES ('1962', '196', '狂风之息', '1', '60', '百里守约开始瞄准，并尝试进行一次狙击，对命中目标造成1050/1200/1350/1500/1650/1800（+250%额外物理加成）点物理伤害并减少其90%移动速度，持续0.5秒；瞄准需要花费2秒的时间，但在瞄准未完成时子弹有可能产生偏移；技能子弹每16秒获得一颗，最多储存3颗（受冷却缩减影响）；', '狂风之息拥有超远的射程和视野范围，是百里守约的核心技能，百里守约主要通过该技能狙击对方制造伤害，可以开启团战或收割残血敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/196/19620.png');",
                "INSERT INTO `skill` VALUES ('1963', '196', '逃脱', '25', '100', '百里守约向后跳跃并向指定方向射击，造成500/700/900（+168%物理加成）点物理伤害并减少其50%移动速度，持续2秒；落地后自身将增加30%移动速度，持续2秒，但普通攻击或技能攻击会立即停止增加移动速度', '逃脱会让百里守约摆脱近身突袭的敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/196/19630.png');",
                "INSERT INTO `skill` VALUES ('1780', '178', '神智侵蚀', '0', '0', '被动：杨戬附近的每个敌方英雄都会使杨戬减少10%所受控制效果的持续时间，最多减少30%', '杨戬身边的敌方英雄越多，所受到的控制时间越低，这使得杨戬在对战时更加的灵动', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/178/17800.png');",
                "INSERT INTO `skill` VALUES ('1781', '178', '逆转乾坤', '8', '60', '杨戬召唤哮天犬并让其向指定方向发起冲锋，对第一个命中的敌人造成150/180/210/240/270/300（+70%物理加成）点物理伤害，如果命中敌人，5秒内可再次施放该技能，施放后杨戬将冲向该敌人并发起一次攻击，造成225/270/315/360/405/450（+105%物理加成）点物理伤害并附加目标已损生命值16%的物理伤害；如果杨戬直接击败目标，将立即刷新逆转乾坤冷却时间', '此技能为非指向性技能，需要预判对手的走位，杨戬可以根据战场情况在第一段技能命中后立即瞬移到敌人身后进行攻击，也可以等敌方位移技能交出后再使用第二段技能瞬移到敌人身后进行攻击，这样更能提升他的击杀率；在将目标击杀后，还可以刷新技能冷却时间；', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/178/17810.png');",
                "INSERT INTO `skill` VALUES ('1782', '178', '虚妄破灭', '12', '70', '杨戬向指定方向横扫，对范围内敌人造成250/300/350/400/450/500（+100%物理加成）点物理伤害并减少50%移动速度，持续2秒；如果命中的目标生命值百分比高于杨戬，将会晕眩1秒；命中敌人后，杨戬的普通攻击将附带80/120/160/200/240/280（+35%物理加成）点真实伤害，持续5秒', 'AOE伤害技能，能减速范围内的敌人，对生命值百分比高于杨戬的敌人造成眩晕效果，所以在杨戬血量很低时这个技能可以对范围内的敌人造成群体眩晕效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/178/17820.png');",
                "INSERT INTO `skill` VALUES ('1783', '178', '根源之目', '30', '100', '杨戬可利用天眼施放3次激光，每次间隔不超过5秒，对路径上的敌人造成250/315/380（+85%物理加成）点物理伤害。第3次将会发出3道激光，同时被多道激光命中，从第二道激光开始只会造成30%的伤害；激光命中敌人时，造成的伤害值的50%会转化为杨戬的生命值', '这个技能需要杨戬预判敌人的走位，可造成可观的AOE伤害，并且杨戬造成的伤害越高，能对自己有更强的治疗效果，第三段技能在敌人聚集时施放会有很高的收益', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/178/17830.png');",
                "INSERT INTO `skill` VALUES ('1740', '174', '树神佑护', '0', '0', '被动：虞姬脱离战斗状态后，树神之力会将下一次普通攻击变更为树神束缚，获得更远的攻击距离并造成200(+200%物理加成)点物理伤害，同时将减少其90%移动速度，持续1.5秒，该效果会不断衰减', '这一技能对于射手来说是一个超强的软性控制，无论是走位失误在野区碰到对方刺客的袭击，或是线上游走gank，都是非常好的撤退和留人技能，在敌人被束缚的时间内，足够虞姬一套技能打出爆炸性的伤害了.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/174/17400.png');",
                "INSERT INTO `skill` VALUES ('1741', '174', '楚歌起', '5', '50', '虞姬蓄力后，向指定方向发射一支携树神加持的强力弩矢，弩矢对路径上第一个敌人造成365/475/585/695/805/915（+180%物理加成）点物理伤害；弩矢每命中一个敌人衰减15%伤害，最低衰减至40%伤害', '无论在线上还是团战中，虞姬都可以用此技能来对对手进行远程消耗，这个技能穿过敌方单位后造成物理伤害，所以团战前消耗到对方更多的敌人会是团战胜利的良好保证.值得注意的是，这个技能蓄力短暂时间后才会施放，所以身为虞姬的操作者需要精准的预判敌人的走位！', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/174/17410.png');",
                "INSERT INTO `skill` VALUES ('1742', '174', '大风来', '15', '80', '虞姬增加30/36/42/48/54/60%移动速度，持续2秒，期间将闪避所有物理攻击；虞姬在技能开启时，会解除减少自身移动速度的效果', '团战中，虞姬一定要保留此技能，等待敌方物理刺客英雄近身切入时施放技能，走位进行疯狂输出.当然，在保证安全的情况下，技能提供的移速加成，也能让你更好的追击敌人，拿下人头！需要注意该技能无法解除控制效果和魔法伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/174/17420.png');",
                "INSERT INTO `skill` VALUES ('1743', '174', '阵前舞', '20', '100', '虞姬冲向目标，在接触目标时将其眩晕0.5秒并触发树神束缚，然后向后跳跃，跳跃时对目标快速射出2支弩矢，每支弩矢造成220/280/340（+120%物理加成）点物理伤害；虞姬在跳跃过程中免疫控制效果', '虞姬的这个技能可以将它当作一个位移技能来使用，在虞姬2技能使用后，还能备有一手位移技能，并且造成高额的输出，无疑体现了虞姬超强的生存能力.与此同时，高额的物理伤害通常也作为一个击杀镜头的漂亮收尾', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/174/17430.png');",
                "INSERT INTO `skill` VALUES ('1700', '170', '强化霰弹', '0', '0', '被动：刘备的火铳是霰弹枪，目标会受到50%总攻击力+35%×额外弹丸数的总伤害；每颗弹丸命中敌方英雄，会增加1层战斗本能，战斗本能会给刘备增加6~10点物理防御和法术防御，持续6秒，最多叠加12层，增加幅度随英雄等级成长；每次射击只有中间的2颗弹丸能够暴击并触发装备效果；建筑不受额外弹丸影响', '刘备的被动技能是他保持站桩输出的强力保证', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/170/17000.png');",
                "INSERT INTO `skill` VALUES ('1701', '170', '双重射击', '8', '60', '刘备增加40%移动速度，持续1.5秒，并在下一次普通攻击变更为两次连射，连射时每颗弹丸会造成120（+40%物理加成）点物理伤害', '这个技能可以提升刘备的追人和撤退能力，并且能增强输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/170/17010.png');",
                "INSERT INTO `skill` VALUES ('1702', '170', '身先士卒', '10', '80', '刘备向指定方向冲撞，对撞击到的敌人造成180/200/220/240/260/280（+70%物理加成）点物理伤害并停止冲撞；同时撞击到的敌人会被击退；若撞击到敌方英雄将减少2秒身先士卒的冷却时间', '这个技能是刘备的控制技能，在观察好局势后灵活选择用来逃跑或者进攻，撞击英雄能减少技能CD时间', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/170/17020.png');",
                "INSERT INTO `skill` VALUES ('1703', '170', '以德服人', '60', '130', '刘备立即解除自身的控制效果，并获得可抵免1100/1650/2200点伤害的护盾，持续6秒；刘备在护盾存在期间免疫控制，同时将对敌方英雄造成伤害的50%转化为护盾值，对小兵和野怪造成伤害的20%转化为护盾值，护盾上限为初始值的150%；释放技能还会获得50/100/150点物理攻击力加成，持续6秒', '刘备大招提供的护盾结合他的被动，使得刘备有足够的身板能在团战混沌中心疯狂输出，开启大招能免疫控制，使得刘备在团战中可以更加随心所欲地输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/170/17030.png');",
                "INSERT INTO `skill` VALUES ('1680', '168', '强力援护', '0', '0', '被动：当牛魔附近500范围内有友方英雄时，牛魔和范围内血量最少的友方英雄会获得200点物理防御和法术防御', '这是牛魔非常重要的保人技能，牛头尽量和血量最少的友方英雄靠近，保护其撤退和输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/168/16800.png');",
                "INSERT INTO `skill` VALUES ('1681', '168', '咆哮之斧', '6', '55', '牛魔向指定方向以大斧横劈，对范围内的敌人造成250/300/350/400/450/500（+110%物理加成）点物理伤害，同时敌方英雄将减少15%的物理攻击力和法术攻击力，持续3秒；如果命中敌方英雄，牛魔下一次普通攻击或技能将额外造成200点法术伤害', '尽量使用此技能命中敌方输出位置，降低其输出伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/168/16810.png');",
                "INSERT INTO `skill` VALUES ('1682', '168', '横行霸道', '10', '80', '牛魔朝指定方向冲锋，对路径上的敌人造成450/530/610/690/770/850（+90%物理加成）点物理伤害并击飞0.75秒；敌人落地后会减少90%移动速度，持续1秒', '可以当做位移技能使用，突进敌人造成击飞及减速效果，对友军为友军增加护盾减少伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/168/16820.png');",
                "INSERT INTO `skill` VALUES ('1683', '168', '山崩地裂', '40', '150', '牛魔短暂蓄力后向指定方向以巨斧猛力劈开大地，对范围内敌人造成550/700/850（+130%物理加成）点物理伤害并将其击飞1秒；同时被劈开的大地被撕裂5秒，对处于其中的敌人每0.5秒造成80/100/120（+20%物理加成）点物理伤害并减少其50%移动速度', '此技能在团战会有巨大的作用，技能命中敌方英雄的数量往往决定了团战的走向，用来进行反手控制是最佳选择', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/168/16830.png');",
                "INSERT INTO `skill` VALUES ('1060', '106', '治愈微笑', '0', '0', '被动：小乔释放技能命中敌人时，会增加25％移动速度，持续2秒', '利用技能命中敌人，被动提供的移速加成能让小乔更好的追击敌人或撤退', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/106/10600.png');",
                "INSERT INTO `skill` VALUES ('1061', '106', '绽放之舞', '5', '60', '小乔向指定方向扔出一把回旋飞行的扇子，会对第一个命中的敌人造成450/540/630/720/810/900（+80％法术加成）点法术伤害，每次命中后伤害都会衰减20％，最低衰减至初始伤害的40％', '小乔的主要输出技能，这个技能命中直线上更多的敌人能有效打出AOE伤害，在面对敌人的追击时使用此技能命中率会更高', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/106/10610.png');",
                "INSERT INTO `skill` VALUES ('1062', '106', '甜蜜恋风', '14', '80', '小乔在指定区域召唤出一道旋风，对区域内敌人造成300/340/380/420/460/500（+50％法术加成）点法术伤害并击飞1.5秒', '小乔的主要控制技能，利用2技能击飞敌人能极大提高1技能的命中率，打出连招爆发伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/106/10620.png');",
                "INSERT INTO `skill` VALUES ('1063', '106', '星华缭乱', '42', '140', '小乔召唤流星并不断向附近的敌人坠落，召唤持续5秒，每颗流星会造成400/500/600（+100％法术加成）点法术伤害，每个敌人最多承受4次攻击，当多颗流星命中同一目标时，从第二颗流星开始将只造成50％伤害', '团战时利用此群攻技能能打出不错的伤害，但注意走位保证生存', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/106/10630.png');",
                "INSERT INTO `skill` VALUES ('1490', '149', '君主野望', '0', '0', '被动：刘邦第三次普通攻击将附带最大生命值4-8%的法术伤害，增加幅度随英雄等级成长，该效果无法对机关造成伤害', '增加自己的最大生命值来使普攻输出增高。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/149/14900.png');",
                "INSERT INTO `skill` VALUES ('1491', '149', '霸业之盾', '10', '75', '刘邦开启护盾将自身笼罩，持续5秒，护盾可抵免500/650/800/950/1100/1250（+168）（最大生命值的5/6/7/8/9/10%）点伤害，结束时对周围造成400/520/640/760/880/1000（+134）（最大生命值的4/4/5/6/7/8%）点法术伤害；2秒后再次施放可立即结束护盾，若护盾被击破，则不会造成伤害', '利用护盾吸收尽可能多的伤害并且不让护盾被击破来贴近敌方造成伤害和减速效果。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/149/14910.png');",
                "INSERT INTO `skill` VALUES ('1492', '149', '双重恐吓', '10', '70', '刘邦蓄力后挥剑冲锋300-500距离，对路径上敌人造成150/180/210/240/270/300~600（+60%物理加成）点法术伤害和0.5-1秒晕眩效果，冲锋距离、攻击伤害和晕眩时长和蓄力时间成正比。蓄力达2秒后各效果强度达到上限，最多可保存蓄力状态5秒，蓄力期间若取消或被打断，该技能会执行40%冷却时间', '强力控制技能，近距离同时命中多个敌人是收益最大的一种方式。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/149/14920.png');",
                "INSERT INTO `skill` VALUES ('1493', '149', '统御战场', '70', '140', '刘邦指定一名队友并开始原地吟唱，持续2.2秒，期间刘邦和该队友将减少40%所受到的伤害，并且1技能损人利己将以援护队友作为添加护盾目标；吟唱后刘邦将传送到该队友位置；传送后刘邦增加30%移动速度，持续1.5秒；同时会生成一个跟随自身的法术场，法术场为范围内队友提供60/105/150点物理防御和法术防御，持续6秒', '刘邦的3技能现在能为队友提供高额的免疫伤害效果但持续时间较短，在敌方集火队友时及时使用；传送后的刘邦会提供免伤法术场，尽可能覆盖到更多的队友。', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/149/14930.png');",
                "INSERT INTO `skill` VALUES ('1420', '142', '咒术火焰', '0', '0', '被动：安琪拉的技能会灼烧敌人，每层灼烧效果会造成11（+2%法术加成）点法术伤害，灼烧效果最多叠加10层', '咒术火焰是安琪拉的核心技能，她能够使安琪拉的所有技能伤害都附加一个额外魔法伤害，依靠这个被动技能效果，安琪拉在施放技能持续攻击敌人时，能够造成成吨的伤害.', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/142/14200.png');",
                "INSERT INTO `skill` VALUES ('1421', '142', '火球术', '6', '50', '安琪拉召唤5颗火球朝指定位置攻击，每颗火球对敌人造成350/390/430/470/510/550（+30%法术加成）点法术伤害，当多颗火球命中同一目标时，从第二颗火球开始将只造成30%伤害，当火球命中敌方英雄时会停止移动并销毁', '非指向性技能，需要预判敌方走位释放.敌人受到控制效果时能极大提高此技能命中率', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/142/14210.png');",
                "INSERT INTO `skill` VALUES ('1422', '142', '混沌火种', '10', '60', '安琪拉朝指定方向释放混沌火种，火种会在命中敌人或飞行到最远距离时裂变为火焰漩涡，火焰漩涡会继续缓慢的向前飞行并且每0.5秒对周围的敌人造成130/145/160/175/190/205（+17%法术加成）点法术伤害，持续3秒；同时范围内的敌人会减少50%移动速度，持续1.5秒；当火种直接命中敌人时还会将其晕眩1秒', '变为火焰漩涡时，会对第一个敌人造成短暂的眩晕效果，所以尽量在眩晕期释放其他技能会极大提高命中率和输出伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/142/14220.png');",
                "INSERT INTO `skill` VALUES ('1423', '142', '炽热光辉', '20', '100', '安琪拉引导魔力朝指定方向释放炽热光辉，持续3秒，对触碰的敌人每0.3秒造成150/190/230（+21%法术加成）点法术伤害并获得可抵免600/900/1200（+80%法术加成）点伤害的炽热护盾，护盾存在期间不会受到控制效果，施法1秒后再次释放该技能可以取消引导', '安琪拉在引导过程中，是可以通过左摇杆来控制自己的当前朝向，精准控制，能够让敌人无处可逃.在释放技能同时，安琪拉会获得炽热护盾，护盾存在期间不会受到敌方的控制效果.当然，如果你觉得释放技能位置不好，可以随时再次点击技能按钮取消技能施法', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/142/14230.png');",
                "INSERT INTO `skill` VALUES ('1460', '146', '月光之舞', '0', '0', '被动：露娜对新目标的第一次普通攻击会向敌人发起冲锋；露娜的第三次普通攻击将会造成范围伤害并标记敌人，但无法标记机关', '这是露娜团战输出的主要AOE伤害之一，更重要的是能进行标记刷新大招CD，使得技能连招更多样化', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/146/14600.png');",
                "INSERT INTO `skill` VALUES ('1461', '146', '弦月斩', '5', '50', '露娜向指定方向挥出一道月光冲击波，对命中的敌人造成350/430/510/590/670/750（+51%法术加成）点法术伤害并标记敌人', '主要的作用是进行标记，刷新大招CD.同时有一定的施法范围，在线上进行消耗非常不错', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/146/14610.png');",
                "INSERT INTO `skill` VALUES ('1462', '146', '炙热剑芒', '8', '50', '露娜将剑插入大地，以炙热剑芒牵引附近的敌人靠近自己造成120/135/150/165/180/195（+31%法术加成）点法术伤害并眩晕0.5秒，同时获得可抵免400/480/560/640/720/800（+80%法术加成）点伤害的护盾并减少其50%移动速度，持续2秒；同时会标记被牵引的敌人', '该技能可以控制范围内的敌人，可以配合队友留人，也可在晕眩时间内打出被动，造成更多伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/146/14620.png');",
                "INSERT INTO `skill` VALUES ('1463', '146', '新月突击', '25', '80', '露娜向指定方向发起突击，对路径上敌人造成500/625/750（+60%法术加成）点法术伤害；如果露娜命中了被标记的敌人，将会刷新新月突击的冷却时间', '露娜的大招是她最重要的技能，在1技能和被动都能重置CD的情况下，灵活利用标记可以在一波团战中一直使用大招进行突进和输出', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/146/14630.png');",
                "INSERT INTO `skill` VALUES ('1800', '180', '炙炼火种', '0', '0', '被动：当哪吒用三尖枪和乾坤圈，混天绫进行攻击时会在敌人身上残留炙炼火种，对敌人每秒造成35（+26%物理加成）点真实伤害，持续3秒，并且炙炼火种会减少其25%的生命回复效果', '这个技能使得哪咤能对对手持续造成伤害，在面对坦克的时候，能限制他的回血效果', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/180/18000.png');",
                "INSERT INTO `skill` VALUES ('1801', '180', '火焰三尖枪', '5', '55', '哪吒挥动三尖枪攻击四周的敌人，造成300/360/420/480/540/600(+85%物理加成)点物理伤害。每击中一个敌方英雄都会触发1次风火轮，当哪吒触发风火轮时会增加6%移动速度，持续5秒，最多可叠加5层', '范围伤害技能，击中更多单位提升哪咤的移动速度', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/180/18010.png');",
                "INSERT INTO `skill` VALUES ('1802', '180', '混天绫·束', '12', '65', '哪吒用混天绫短暂束缚敌人，然后迅速俯冲向该名敌人并翻越至另一侧，该技能4秒可以再次释放；每击中一名敌人都会触发一次风火轮；释放技能会触发火莲之印；当哪吒触发火莲之印后会减少15/16/17/18/19/20%所受伤害。所受伤害，持续3秒', '这是哪咤的位移技能，利用它可以很好的黏住敌人，但要注意这个技能只对敌方英雄有效', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/180/18020.png');",
                "INSERT INTO `skill` VALUES ('1803', '180', '乾坤·天降', '80', '140', '哪吒升上天空对敌方所有英雄施加乾坤圈，释放后将获得其视野并打断其回城.随后可选择一名敌方英雄并飞冲到其身前，造成500/625/750（+100%物理加成）点物理伤害并将其击退，落地时激发火莲之华，对火莲范围内的敌人每秒施加1次炙炼火种，并增加自身30%韧性；落地时还会触发火莲之印；飞行过程中再次释放技能会立即降落并激发火莲之华', '这个技能有很强的战术意义，可以开启视野看到对手动向，可以单人带线牵扯敌方并快速支援', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/180/18030.png');",
                "INSERT INTO `skill` VALUES ('1050', '105', '友情守护', '0', '0', '被动：廉颇释放技能的过程中处于霸体状态，并且减少20%所受到的伤害', '施放技能减伤，而且不会被控制', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/105/10500.png');",
                "INSERT INTO `skill` VALUES ('1051', '105', '豪情突进', '9', '60', '廉颇向指定方向发起冲锋，对敌人造成115/130/145/160/175/190（+100%物理加成）点物理伤害并将其击飞1秒', '利用突进技能，可击飞敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/105/10510.png');",
                "INSERT INTO `skill` VALUES ('1052', '105', '激情迸发', '12', '70', '廉颇短暂蓄力后爆发为自身提供抵免350/420/490/560/630/700点伤害的护盾，同时对范围内的敌人造成500/560/620/680/740/800（+155%物理加成）点物理伤害并减少其30%移动速度，持续1秒', '12技能连招可打出小爆发伤害', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/105/10520.png');",
                "INSERT INTO `skill` VALUES ('1053', '105', '正义豪腕', '54', '150', '廉颇跳向指定区域连续锤击地面三次，第一次锤击会对范围内敌人造成150/200/250（+80%物理加成）点物理伤害，随后每次递增50%的伤害，并且第一次和第二次锤击时，范围内敌人分别会减少30%和50%移动速度，持续1秒，第三次锤击会将范围内的敌人击飞1.5秒。', '找机会突进技能开团，尽量大招控制到更多敌人', 'http://game.gtimg.cn/images/yxzj/img201606/heroimg/105/10530.png');"};

        String[] insertEquip ={"INSERT INTO `hero_equip` VALUES ('105', '1331,1334,1421,1333,1327,1337', '这是全肉装坦克的出装方法，团战技能有一定输出能力，也有非常强的承伤能力', '1331,1334,1421,13310,1336,1335', '对方物理输出较多情况下，极寒风暴能降低敌方输出的攻击速度，魔女斗篷配合被动能有更大的收益');",
                "INSERT INTO `hero_equip` VALUES ('106', '1226,1232,1424,1233,1236,1238', '暴力的法师全输出装备，能够形成非常高的伤害输出', '1226,1237,1424,1234,1239,1231', '一套比较肉的法师出装，生存能力超强，并且有一定输出能力，适合逆风局出的装备');",
                "INSERT INTO `hero_equip` VALUES ('107', '1131,1133,1421,1332,1334,1337', '半肉半输出的出装思路，能有效提升赵云生存能力和作战能力', '1131,1132,1421,1133,1137,1136', '全输出装备赵云，输出能力极强，但生存能力较弱');",
                "INSERT INTO `hero_equip` VALUES ('108', '1237,1236,1233,1421,1231,1235', '偏肉型的法师出装，使得墨子成为标准法坦，能抗能打', '1237,1232,1239,1424,1231,1236', '全输出装备，墨子后期需要远程进行输出，最后进入收割节奏');",
                "INSERT INTO `hero_equip` VALUES ('109', '1226,1232,1424,1239,1233,1234', '全输出法师出装，后期秒杀敌方刺客和脆皮', '1226,1238,1424,1232,1235,1233', '输出装备为主，带一点生命值属性的法强提高团战的容错率');",
                "INSERT INTO `hero_equip` VALUES ('110', '1226,1232,1231,1424,1233,1234', '全输出法师出装，后期技能释放好会有爆炸伤害', '1226,1238,1234,1424,1235,1233', '输出装备为主，带一点生命值属性的法强提高团战的容错率');",
                "INSERT INTO `hero_equip` VALUES ('111', '1133,1135,1132,1425,1131,1136', '全输出装备，团战能打出爆炸伤害，需要较高的操作技巧', '1132,1136,1425,1131,1138,1335', '输出装为主，配合魔女斗篷，后期团战中有更强的容错率');",
                "INSERT INTO `hero_equip` VALUES ('112', '1133,1135,1132,1425,1131,1136', '全输出装备，团战能打出爆炸伤害，需要较高的操作技巧和队友的保护', '1132,1136,1425,1131,1138,1335', '输出装为主，配合魔女斗篷，后期团战中有更强的容错率');",
                "INSERT INTO `hero_equip` VALUES ('113', '1724,1421,1331,1333,1335,1336', '这是全肉装坦克的出装方法，团战能有强大的保人能力，也有非常强的承伤能力', '1421,1331,1335,1235,1234,1336', '这套装备使得庄周能抗能打，输出能力与保护能力都很强');",
                "INSERT INTO `hero_equip` VALUES ('114', '1421,1331,1335,1333,1336,1332', '这是全肉装坦克的出装方法，团战能有强大的保人能力，也有非常强的承伤能力', '1421,1331,1335,1333,13310,1336', '对方物理输出较多情况下，极寒风暴能降低敌方输出的攻击速度，坦度十足');",
                "INSERT INTO `hero_equip` VALUES ('115', '1240,1232,1233,1424,1234,1235', '这是高渐离走高输出路线的一种出装思路，输出非常高但是很脆，需要有较好的操作和团战处理能力', '1240,1237,1234,1421,1238,1239', '这套装备使得高渐离既有很强的输出能力也能在团战中有较大的容错率.');",
                "INSERT INTO `hero_equip` VALUES ('116', '1133,1421,1135,1338,1331,1337', '拥有强大的物理爆发能力，能够在短时间内秒掉敌方脆皮', '1137,1421,1133,1132,1131,1127', '后期牺牲了一定的爆发，但是持续输出能力更强');",
                "INSERT INTO `hero_equip` VALUES ('117', '1421,1331,1335,13310,1338,1336', '战士的出装思路，能有效提升钟无艳生存能力和持续输出能力', '1421,1331,1333,1335,13310,1336', '坦克的出装思路，能有效提升钟无艳生存能力和控制输出能力');",
                "INSERT INTO `hero_equip` VALUES ('118', '1235,1424,1233,13310,1336,1335', '全输出法师出装，会有较高的法术伤害，但比较脆皮，生存能力较弱，需要比较好的走位技巧', '1724,1423,1235,1334,1336,1335', '辅助装备为主，带一些生命值属性的法强提高生存能力，团战中能持久的对队友进行保护，提升了阵容的容错率');",
                "INSERT INTO `hero_equip` VALUES ('119', '1235,1232,1424,1234,1233,1231', '输出较高的一套法师装备，缺点是比较脆皮，生存能力较弱', '1235,1240,1424,1234,1233,1238', '输出装备为主，带一些生命值属性的法强提高生存能力，团战中能持久的对队友进行保护，持续作战能力超强');",
                "INSERT INTO `hero_equip` VALUES ('120', '1327,1331,1421,1334,1332,1338', '这是全肉装坦克的出装方法，团战技能有一定输出能力，也有非常强的承伤能力', '1327,1724,1422,1334,1336,13310', '这是白起偏团队型的坦克出装，能为团队创造更大的收益');",
                "INSERT INTO `hero_equip` VALUES ('121', '1240,1421,1234,1334,1333,1337', '半肉半输出装备，有一定的输出，配合被动有一定的坦度', '1422,1334,1332,1336,1335,1337', '输出能力略显缺乏，但能够成为一个合格的前排，拥有恐怖的血量和双抗');",
                "INSERT INTO `hero_equip` VALUES ('123', '1131,1133,1421,1332,1334,1337', '半肉半输出装备，在持续输出能力上非常强', '1328,1724,1422,1334,1336,13310', '这是吕布偏团队型的坦克出装，能为团队创造更大的收益，在被动的存在下，他在团战中也会有不俗的伤害');",
                "INSERT INTO `hero_equip` VALUES ('124', '1226,1424,1233,1231,1232,1238', '圣杯增加耗线续航能力，回响之杖增加走位能力，高冷却，高法术攻击可以直观的打出大量伤害', '1234,1424,1239,1231,1232,1238', '冰霜法杖提供减速，使得敌人更难摆脱火区，该出装拥有极高的生存能力和控制能力，但对蓝buff依赖较大');",
                "INSERT INTO `hero_equip` VALUES ('125', '1137,1423,1132,1133,1131,1138', '刺客出门装，中期补出泣血之后，元歌的持续作战能力会大幅提高，全输出的元歌会拥有强大的爆发能力，但傀儡较为脆弱，需要把握时机入场', '1137,1422,13310,1138,1131,1127', '刺客出门装，两件装备会提供一定的血量和物理防御使得傀儡的容错率较高，名刀的被动效果会让战斗手段较多的元歌有一定操作空间，避免被秒杀');",
                "INSERT INTO `hero_equip` VALUES ('126', '1137,1423,1132,1131,1325,1338', '前中期以输出装为主，后期补上一定的血量装备以保证持续输出能力', '1724,1421,1331,1332,1325,1333', '全肉装夏侯惇，能抗能打，可以承受巨量伤害');",
                "INSERT INTO `hero_equip` VALUES ('127', '1424,1233,1235,1232,1231,1238', '暴力的法师全输出装备，能够形成非常高的伤害输出', '1421,1240,1235,1233,1232,1234', '一套比较肉的法师出装，生存能力超强，并且有一定输出能力，适合逆风局出的装备');",
                "INSERT INTO `hero_equip` VALUES ('128', '1126,1132,1133,1423,1137,1126', '全输出曹操，伤害爆炸，但是很脆，对操作要求较高', '1132,1133,1421,1328,13310,1337', '半肉半输出曹操出装，在爆发和生存能力上都很强');",
                "INSERT INTO `hero_equip` VALUES ('129', '1331,1335,1421,1334,1332,1338', '这是全肉装坦克的出装方法，团战技能有一定输出能力，也有非常强的承伤能力', '1333,1724,1422,1334,1336,13310', '这是典韦偏团队型的坦克出装，能为团队创造更大的收益');",
                "INSERT INTO `hero_equip` VALUES ('130', '1137,1421,1134,1135,1327,1338', '前中期以输出装为主，后期补上一定的血量装备以保证持续输出能力', '1522,1421,1134,1327,1334,1337', '打野出装思路，半肉宫本，主要利用控制效果配合队友输出');",
                "INSERT INTO `hero_equip` VALUES ('131', '1126,1132,1422,1137,1138,1125', '全输出装备李白，输出能力极强，但团战容错率较低', '1126,1132,1421,1137,1337,1332', '输出装提升中期的输出能力，防御型装备能有效提升李白生存能力和持续作战能力');",
                "INSERT INTO `hero_equip` VALUES ('132', '1126,1425,11311,1135,1155,1335', '拥有了一定的攻速和纯净苍穹的免伤效果之后，马可波罗将会具备一定的战斗能力，魔女斗篷能够有效的反制反伤甲和爆发型法师的秒杀能力。', '1533,1422,1126,11311,1132,1155', '打野刀可以后置升级，优先做出末世增加续航和战斗能力，选择双吸避免反伤甲克制问题，但是需要小心走位避免被法师秒杀。');",
                "INSERT INTO `hero_equip` VALUES ('133', '1135,1133,1132,1425,1131,1136', '全输出装备，团战能打出爆炸伤害，需要较高的操作技巧', '1132,1136,1425,1131,1138,1335', '输出装为主，配合魔女斗篷，后期团战中有更强的容错率');",
                "INSERT INTO `hero_equip` VALUES ('134', '1137,1421,1134,1126,1327,1338', '前中期以输出装为主，后期补上一定的血量装备以保证持续输出能力', '1522,1421,1134,1327,1334,1337', '打野出装思路，半肉达摩，主要利用控制效果配合队友输出');",
                "INSERT INTO `hero_equip` VALUES ('135', '1421,1331,1335,13310,1327,1332', '这是全肉装坦克的出装方法，团战技能有一定输出能力，也有非常强的承伤能力', '1724,1421,1331,1333,1335,1332', '多以军团荣耀、极寒风暴等装备作为一个团队型的坦克，能对队友起到强大的保护作用');",
                "INSERT INTO `hero_equip` VALUES ('136', '1424,1233,1235,1232,1231,1238', '暴力的法师全输出装备，能够形成非常高的伤害输出，但需依赖较好的走位与操作', '1421,1240,1235,1233,1232,1234', '一套比较肉的法师出装，生存能力超强，并且有一定输出能力，适合逆风局出的装备');",
                "INSERT INTO `hero_equip` VALUES ('139', '1137,1421,1134,1135,1327,1338', '前中期以输出装为主，后期补上一定的血量装备以保证持续输出能力', '1522,1421,1134,1327,1334,1337', '打野出装思路，半肉老夫子，主要利用控制效果配合队友输出');",
                "INSERT INTO `hero_equip` VALUES ('140', '1422,1137,13310,1334,1333,1327', '这套装备使得关羽在后期能有一定的输出，并且抗击打能力也很强', '1421,1724,1334,1336,1332,1337', '这使得关羽成为不折不扣的肉坦，团战中只需要冲到混沌中心，干扰敌人的输出');",
                "INSERT INTO `hero_equip` VALUES ('141', '1240,1232,1235,1424,1233,1234', '全输出法师出装，后期秒杀敌方刺客和脆皮，但容错率较低，需要一定的操作技巧', '1240,1234,1421,13310,1334,1234', '这是貂蝉偏2法坦的出装思路，团战伤害稳定，生存能力很强');",
                "INSERT INTO `hero_equip` VALUES ('142', '1424,1232,1231,1233,1236,1238', '暴力的法师全输出装备，能够形成非常高的伤害输出', '1424,1237,1233,1234,1239,1231', '一套比较肉的法师出装，生存能力超强，并且有一定输出能力，适合逆风局出的装备');",
                "INSERT INTO `hero_equip` VALUES ('144', '1331,1334,1421,1333,1327,1337', '这是全肉装坦克的出装方法，团战技能有一定输出能力，也有非常强的承伤能力', '1724,1334,1421,1333,1336,1335', '多以军团荣耀、极寒风暴等装备作为一个团队型的坦克，能对队友起到强大的保护作用');",
                "INSERT INTO `hero_equip` VALUES ('146', '1237,1232,1424,1234,1236,1238', '这套装备既保证了露娜核心的爆发输出能力，也增强了她的身板，使得生存能力大大提升', '1237,1234,1421,1238,13310,1334', '这是露娜法坦的出装思路，虽然损失了部分输出能力，但是能保证露娜团战的生存力，能持续作战');",
                "INSERT INTO `hero_equip` VALUES ('148', '1424,1233,1235,1232,1231,1238', '暴力的法师全输出装备，能够形成非常高的伤害输出，但需依赖较好的走位与操作', '1724,1423,1235,1226,1232,1238', '一套比较肉的法师出装，生存能力超强，并且有一定输出能力，适合逆风局出的装备');",
                "INSERT INTO `hero_equip` VALUES ('149', '1331,1421,1327,1335,1332,1337', '后期拥有高额血量和双抗，配合技能输出能力也不弱', '1331,1423,1327,1336,1332,1337', '后期冷却缩减很高，可以频繁释放技能帮助队友，并且自身的血量和抗性也不低');",
                "INSERT INTO `hero_equip` VALUES ('150', '1126,1132,1422,1137,1138,1125', '全输出装备韩信，输出能力极强，但团战容错率较低', '1126,1132,1421,1137,1337,1332', '输出装提升中期的输出能力，防御型装备能有效提升韩信生存能力和持续作战能力');",
                "INSERT INTO `hero_equip` VALUES ('152', '1424,1233,1235,1232,1231,1238', '暴力的法师全输出装备，能够形成非常高的伤害输出', '1421,1240,1235,1233,1232,1239', '一套比较肉的法师出装，生存能力超强，并且有一定输出能力，适合逆风局出的装备');",
                "INSERT INTO `hero_equip` VALUES ('153', '1126,1132,1422,1137,1138,1125', '全输出装备兰陵王，输出能力极强，只要技能命中，一套带走|', '1126,1132,1421,1137,1337,1332', '输出装提升中期的输出能力，防御型装备能有效提升兰陵王生存能力和持续作战能力');",
                "INSERT INTO `hero_equip` VALUES ('154', '1137,1422,1133,1132,1131,1337', '后期输出非常高，缺点是容错率低，需要较好的操作技巧', '1137,1422,1333,1335,1131,1337', '牺牲一部分的输出，保证自己能够尽可能的站在战场上');",
                "INSERT INTO `hero_equip` VALUES ('156', '1424,1233,1235,1232,1240,1238', '前期充足的伤害以及清兵能力，对线有信心，全输出装备，位团队提供充足的持续法术输出', '1426,1233,1226,1235,1232,1238', '高游走速度和冷却缩减，前中期可多进行gank，后期补充伤害和一定的生存能力');",
                "INSERT INTO `hero_equip` VALUES ('157', '1237,1232,1424,1236,1234,1238', '这是不知火舞的高爆发法师出装路线，但生存能力一般，需要较好的操作', '1237,1234,1421,1238,1335,1337', '这套装备使得不知火舞既有很强的输出能力也能在团战中有较大的容错率，能进行持续的输出');",
                "INSERT INTO `hero_equip` VALUES ('162', '1126,1132,1422,1137,1138,1125', '全输出装备娜可露露，输出能力极强，但团战容错率较低', '1126,1132,1421,1137,1337,1332', '输出装提升中期的输出能力，防御型装备能有效提升娜可露露生存能力和持续作战能力');",
                "INSERT INTO `hero_equip` VALUES ('163', '1137,1422,1134,1132,1131,1337', '装备效果配合橘又京被动能打出更加高额的伤害', '1137,1421,1131,1328,1338,1337', '橘右京以防守为主的出装，依靠控制与技能基础伤害尽可能在逆风局中帮助队友');",
                "INSERT INTO `hero_equip` VALUES ('166', '1422,1331,13310,1137,1335,1333', '半肉半输出的出装思路，能保证亚瑟的生存能力同时提供给他持续的作战能力', '1426,1131,1132,1134,1133,1137', '全输出装备亚瑟，输出能力极强，但生存能力较弱，需要一定的操作技巧');",
                "INSERT INTO `hero_equip` VALUES ('167', '1137,1421,1133,1132,1134,1337', '全输出孙悟空，需要较高的操作技巧，能打出高额伤害', '1137,1421,1133,1334,1333,1138', '半肉半输出孙悟空，能保证自己更容易存活');",
                "INSERT INTO `hero_equip` VALUES ('168', '1421,1331,1335,1333,1336,1332', '这是全肉装坦克的出装方法，团战技能有一定输出能力，也有非常强的承伤能力', '1724,1421,1331,1333,1335,1332', '多以军团荣耀、极寒风暴等装备作为一个团队型的坦克，能对队友起到强大的保护作用');",
                "INSERT INTO `hero_equip` VALUES ('169', '1126,1425,1135,1133,1131,1337', '射手出门装，末世配合后羿的被动技能可以大幅度提升后羿的普攻输出，超高爆发输出，破甲弓在中后期可以对敌方坦克构成较大威胁', '1126,1425,1135,1133,1132,1337', '射手出门装，末世配合后羿的被动技能，可以大幅度提升后羿的普攻输出，泣血之刃配合末世，结合后羿的多重射击，给后羿提供爆炸伤害和不俗的吸血效果。');",
                "INSERT INTO `hero_equip` VALUES ('170', '1137,1421,1132,13310,1327,1334', '前中期以输出装为主，后期补上一定的血量装备以保证持续输出能力', '1522,1421,1137,13310,1327,1335', '打野出装思路，半肉刘备，主要利用控制效果进行持续输出');",
                "INSERT INTO `hero_equip` VALUES ('171', '1421,1331,1335,1333,1336,1332', '这是全肉装坦克的出装方法，团战技能有一定输出能力，也有非常强的承伤能力', '1724,1421,1331,1333,1335,1332', '多以军团荣耀、极寒风暴等装备作为一个团队型的坦克，能对队友起到强大的保护作用');",
                "INSERT INTO `hero_equip` VALUES ('173', '1133,1135,1132,1425,1131,1136', '全输出装备，团战能打出爆炸伤害，需要较高的操作技巧', '1132,1136,1425,1328,1335,1128', '输出装为主，配合魔女斗篷，后期团战中有更强的容错率');",
                "INSERT INTO `hero_equip` VALUES ('174', '1133,1135,1132,1425,1131,1136', '全输出装备，团战能打出爆炸伤害，需要较高的操作技巧', '1132,1136,1425,1131,1138,1335', '输出装为主，配合魔女斗篷，后期团战中有更强的容错率');",
                "INSERT INTO `hero_equip` VALUES ('175', '1237,1234,1421,1238,1335,1337', '这套装备使得不知火舞既有很强的输出能力也能在团战中有较大的容错率，能进行持续的输出', '1237,1234,1421,13310,1334,1332', '这套装备使得钟馗作为一个法式坦克，能无畏的冲入战场对对手进行干扰');",
                "INSERT INTO `hero_equip` VALUES ('176', '1724,1426,1233,1336,1335,1238', '辅助出门装，中前期移动速度和一定的血量可以提高杨玉环的持续作战能力，杨玉环能从冷却缩短的属性中提高被动触发的频率，其技能百分比的机制可以优先考虑自保', '1233,1423,1235,1232,1239,1238', '法师出门装，痛苦面具配合自身被动的百分比伤害，可以迅速的对敌方进行消耗，辉月配合大招的机制，可以在一场战斗中，完美的避开敌方的爆发期');",
                "INSERT INTO `hero_equip` VALUES ('177', '1135,1133,1132,1425,1131,1136', '全输出装备，团战能打出爆炸伤害，需要较高的操作技巧', '1132,1136,1425,1131,1138,1335', '输出装为主，配合魔女斗篷，后期团战中有更强的容错率');",
                "INSERT INTO `hero_equip` VALUES ('178', '1331,1422,1134,1334,1138,1338', '杨戬后期有足够的输出和承受伤害的能力，破军配合1技能被动，收割能力超强', '1137,1423,1132,1131,1325,1338', '前中期以输出装为主，后期补上一定的血量装备以保证持续输出能力');",
                "INSERT INTO `hero_equip` VALUES ('179', '1226,1424,1240,1234,1232,1238', '圣杯提供了客观的续航能力，配合噬神之书可以让女娲在线上保持一定的压制能力，虽然爆发力不高，但是消耗和对后排直接攻击的能力，能让女娲保持较高的竞争力', '1233,1424,1240,1232,1231,1238', '传统的法师出装，对于技能的准确度有一定的要求，自身对于蓝buff有一定的依赖，高额的法术攻击，可以让矩阵持续的更久，同时可以对前排造成更多的伤害');",
                "INSERT INTO `hero_equip` VALUES ('180', '1331,1422,1137,1335,1333,1337', '后期哪咤出装以抗性装备为主', '1724,1421,1331,1332,1325,1333', '全肉装哪咤，能抗能打，可以承受巨量伤害');",
                "INSERT INTO `hero_equip` VALUES ('182', '1226,1423,1233,1234,1232,1231', '爆发周期较短，回复能力较高，可以频繁使用技能消耗，由于爆发周期较短，多次爆发后，可以逼迫对方露出破绽', '1235,1423,1233,1231,1232,1238', '每一次命中都会造成高额伤害，每次释放技能都是对于对方的威慑，只需一次命中就会迫使对方回撤或直接造成死亡');",
                "INSERT INTO `hero_equip` VALUES ('183', '1331,1421,1134,1334,1133,1332', '这套装备使得后期雅典娜具有很强的承伤能力，并且输出可观，能黏住敌方后排输出', '1137,1421,1134,1338,1334,13310', '这套装备在后期雅典娜需要切入敌方后排，对敌方后排进行持续输出，需要一定的操作技巧');",
                "INSERT INTO `hero_equip` VALUES ('184', '1724,1423,1226,1336,12211,1238', '以肉装和法术肉装为主，增强自保能力，技能配合装备能对团队形成很好的保护能力', '1226,1423,1234,1235,12211,1233', '输出法师型出装，需要有较好的走位与操作技巧，伤害输出很高');",
                "INSERT INTO `hero_equip` VALUES ('186', '1724,1423,1226,1336,12211,1238', '以肉装和法术肉装为主，增强自保能力，技能配合装备能对团队形成很好的保护能力', '1724,1421,1331,1332,1325,1333', '全肉装太乙真人，可以承受巨量伤害');",
                "INSERT INTO `hero_equip` VALUES ('187', '1422,1334,1336,1235,1234,1337', '法坦装备，在保证自己生存能力的同时拥有不菲的输出', '1422,1334,1332,1336,1335,1337', '输出能力略显缺乏，但能够成为一个合格的前排，拥有恐怖的血量和双抗');",
                "INSERT INTO `hero_equip` VALUES ('189', '1421,1235,1234,13310,1334,1231', '法术装配合一定肉装，使得其输出和承伤效果更平均', '1424,1235,1335,1234,1231,1333', '半肉半输出装备');",
                "INSERT INTO `hero_equip` VALUES ('190', '1233,1424,1240,1234,1232,1238', '整体出装血量较高，配合噬神之书生存能力极强', '1226,1424,1240,1234,1232,1238', '配合诸葛亮大招的斩杀效果，圣杯的特性将会充分发挥.并且冷却缩减可以减少技能2时空穿梭的储存时间，提升诸葛亮的机动性.');",
                "INSERT INTO `hero_equip` VALUES ('191', '1226,1426,1723,13310,1235,1238', '在兼备输出能力的同时保证自己一定的肉度来更久的坚持在战场上', '1234,1422,1336,1335,1333,1235', '后期主要作用是控制和前排抗伤害，输出主要靠百分比伤害的痛苦面具');",
                "INSERT INTO `hero_equip` VALUES ('192', '1135,1133,1132,1425,1131,1136', '全输出装备，团战能打出爆炸伤害，需要较高的操作技巧', '1132,1136,1425,1131,1138,1337', '输出装为主，配合魔女斗篷，后期团战中有更强的容错率');",
                "INSERT INTO `hero_equip` VALUES ('193', '1137,1421,1134,1138,1327,1338', '前中期以输出装为主，后期补上一定的血量装备以保证持续输出能力', '1522,1421,1134,1327,1334,1337', '打野出装思路，半肉铠，主要利用控制效果配合队友输出');",
                "INSERT INTO `hero_equip` VALUES ('194', '1331,1421,1335,1336,1332,1337', '坦克出门装，红莲斗篷配合大招被动可以持续对附近敌人造成伤害，超强防御能力，配合各种被动效果可以持续在人群中输出。', '1331,1421,1137,1327,1335,1333', '战士出门装，中前期可以打出较高的伤害，对敌方后排威胁较大。后期通过装备大幅度提升自身的生存能力，增强持续输出。');",
                "INSERT INTO `hero_equip` VALUES ('195', '1533,1422,1137,13310,1138,1337', '前期打野堆叠攻速方便清野，快速升级保证gank效率，高攻击力能让收割变得更加容易', '1137,1422,1132,13310,1337,1138', '能快速合成输出风暴大剑装备，吸血装保证玄策线上的续航能力。');",
                "INSERT INTO `hero_equip` VALUES ('196', '1137,1421,1132,1131,1138,1337', '前中期利用技能范围优势，可以选择纯输出装备，最后一件选择贤者庇护增加容错率', '1137,1421,1132,1131,1125,1138', '全输出装备，对于2技能的位置选择要求更高，敌方回复能力比较突出时，可以选择制裁之刃');",
                "INSERT INTO `hero_equip` VALUES ('197', '1236,1423,1235,1234,1239,1232', '依靠普通攻击的法术伤害，可以进行对线压制，弈星需要堆叠最大血量，避免一被消耗就触发被动，陷入危险的状况', '1226,1424,1233,1239,1231,1232', '伤害略低，但是不依赖蓝buff，并且拥有极高的冷却缩短，如果敌方刺客，战士过多，那么可以优先选择辉月进行自保');",
                "INSERT INTO `hero_equip` VALUES ('198', '1235,1422,1233,1334,1333,1234', '坦克出门装，痛苦面具的法穿效果和被动可以对敌人有效造成伤害，兼具生存和法术输出，以及不错的粘人能力', '1235,1422,1234,1334,1236,1333', '中前期可以打出较高的伤害，并且软控效果十分出色，提升技能减速能力，并且自身移速提升，更容易靠近敌人');",
                "INSERT INTO `hero_equip` VALUES ('199', '1126,1422,1132,1133,1127,1138', '超高的续航能力，可以让公孙离持续作战，全输出装备，利用公孙离灵活的特性，可以对中后排造成巨大的威胁', '1126,1425,11311,1131,1135,1337', '射手出门装，纯净苍穹可以避免阿离被控制秒杀，同时急速战靴可以增加阿离站桩输出的能力，常规射手神装，后期可以打出爆炸输出');",
                "INSERT INTO `hero_equip` VALUES ('501', '1722,1426,1237,1234,1334,1337', '辅助出门装，跑线支援迅速，续航较好，作为中场辅助，前可支援坦克，后可增强输出，自身也有一定坦度，不会轻易死去', '1233,1424,1235,1234,1231,1238', '法术出门装，主要针对敌方制造伤害和削弱其对抗能力，高额法术攻击能让大招的真实伤害对坦克和后排造成巨大威胁');",
                "INSERT INTO `hero_equip` VALUES ('502', '1523,1422,1134,1133,1132,1138', '打野出门装，中期在装备支持下，清野速度到达巅峰，凭借极强的野区入侵能力，扫荡敌方野区获得优势，后期团战时，位于团队侧翼，对敌方后排进行威慑', '1134,1422,1132,1133,1138,1337', '战士出门装，此时裴擒虎可配合打野进行游走，寻找对方突破口。拥有贤者庇护后，裴擒虎可以从侧翼主动出击，开启团战');",
                "INSERT INTO `hero_equip` VALUES ('503', '1137,1422,1327,1334,1333,1138', '狂铁装备初步成型，在高能状态下无惧大部分挑战，但对法术抗性较低，需要注意对手类型，神装后，狂铁爆发和生存都比较强大，可以在团战中发挥巨大的威胁', '1137,1421,1131,1327,1138,1127', '狂铁对于技能的依赖十分巨大，暗影战斧和破甲弓的冷却属性对于狂铁来说十分珍贵，狂铁常常通过绝地反击获胜，名刀·司命对于狂铁来说无异于第二次反击的机会');",
                "INSERT INTO `hero_equip` VALUES ('504', '1233,1424,1232,1235,1234,1238', '通过博学者，中期米莱狄就会获得高额的法术攻击，使得机械仆从具有一定的战斗力，虽然缺少一定的冷却缩减属性，但是利用兵线生成的仆从依然可以保持推进能力。', '1226,1423,1233,1232,1235,1238', '更加偏向对线消耗，通过1技能较远的攻击距离压制对手，即使离开兵线也可以拥有大量的机械仆从为米莱狄作战，随时保持战斗能力');"};
        String[] insertInscription = {"INSERT INTO `hero_inscription` VALUES ('105', '1504,3503,2501', '廉颇在团战中吸收伤害，骗取敌方关键技能，因为技能有免控效果，蓝色选择生命值提升，绿色选择双防提升让廉颇在前中期就能抗更多伤害，由于技能都是物理收益，异变的攻击和穿透效果可以给廉颇带来技能伤害的提升。');",
                "INSERT INTO `hero_inscription` VALUES ('106', '1514,3515,2520', '狩猎提升10％移速加成，对于被动具有加速效果的小乔来说，适合放风筝，可以进行中远距离的输出，机动性高。心眼和梦魇提升法穿效果。对于小乔这种伤害比较高的英雄来说，增加法穿可以在对方出法抗时也打出高额伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('107', '1504,3514,2517', '100的物穿使赵云在前期击杀脆皮上更加有效，在面对面前排时也有一定的击杀能力，45的物理攻击也是提高了前期的伤害量，而且在清野上也更加的快速，10%的移速加成是为了赵云在gank、支援、追击、逃跑时跑的更快。');",
                "INSERT INTO `hero_inscription` VALUES ('108', '1514,3516,2520', '梦魇的法术攻击和穿透搭配墨子的高法强可以快速清线，消耗敌人。狩猎可使墨子更快的打出普攻的被动。而怜悯的10%冷却可让墨子的技能冷却更快');",
                "INSERT INTO `hero_inscription` VALUES ('109', '1514,3516,2512', '妲己是一个拥有超强爆发的法师英雄，231可以秒杀大多数脆皮英雄，红色梦魇可以提供法术强度和穿透，蓝色轮回可以提升法术强度和吸血赖线能力，绿色怜悯可以减少CD，在中后期几秒就可以释放一套231连招秒人。');",
                "INSERT INTO `hero_inscription` VALUES ('110', '1520,3515,2520', '嬴政前期较其他法师类英雄普攻伤害很高，而且带有穿透效果，适合带攻速类铭文提高普攻输出，提高清兵速度，移速提高了嬴政的支援速度和自保能力。');",
                "INSERT INTO `hero_inscription` VALUES ('111', '1519,3514,2517', '蓝色隐匿移速方便孙尚香更好的移动寻找输出位置，团战支援也比较迅速，物理攻击可以提伤害。绿色鹰眼，穿透护甲后孙尚香的攻击伤害会更高。前期在有祸源铭文的情况下，暴击更多，清野，清兵线的速度更快，发育更快。');",
                "INSERT INTO `hero_inscription` VALUES ('112', '1504,3514,2517', '鲁班七号的核心玩法是利用1技能后的被动扫射在短时间内打出超额爆发，百穿的增益可以确保鲁班七号的有效伤害，攻速收益可以帮鲁班七号更容易叠被动，移速可以帮鲁班增加逃生能力。');",
                "INSERT INTO `hero_inscription` VALUES ('113', '1514,3515,2512', '新版本的庄周不仅仅是一个只能辅助的坦克，其被动结合二技能叠起四层被动，可打出爆炸伤害。庄周佩戴输出铭文，提高法术攻击、法术吸血、法术穿透的能力，让庄周前期也能拥有很强的输出能力，为团队建立优势。');",
                "INSERT INTO `hero_inscription` VALUES ('114', '1512,3509,2515', '宿命增加刘禅的坦克能力；调和，配合1技能控制效果，可以黏住大多数核心输出；刘禅的技能有控制和拆塔的效果，虚空的冷却缩减可以起到加快拆塔速度的作用。');",
                "INSERT INTO `hero_inscription` VALUES ('115', '1514,3515,2503', '高渐离是一名AOE输出爆炸的法师英雄，高额的法术收益和法穿可以帮助他打出更多的伤害，吸血恢复能力可以提升高渐离的赖线能力，打团时利用免伤和恢复可以有更好的生存能力，攻速加成可以更容易组合技能打出被动伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('116', '1510,3514,2520', '选择无双提升暴击效果，配合无尽大大提升阿轲的爆发能力，狩猎的移速配合位移大招，可以让阿轲更轻松接近对手完成刺杀，攻速可以让阿轲在背击时可以配合技能完成普攻，鹰眼的物穿效果可以为阿轲提供稳定的输出能力。');",
                "INSERT INTO `hero_inscription` VALUES ('117', '1504,3514,2515', '调和提高最大生命以及移速加成，提升回复能力；鹰眼和异变提高物理攻击和物理穿透，在一定程度上的提高了钟无艳的伤害，抗伤害的同时自身也可以打出伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('118', '1514,3518,2520', '孙膑作为一个辅助，他需要高频率的释放技能来辅助队友。回声提供冷却缩减与双抗，增加续航能力。梦魇适当地增加伤害。狩猎增加移速可以提升机动性。');",
                "INSERT INTO `hero_inscription` VALUES ('119', '1517,3515,2520', '对扁鹊来说最重要的是攻速和冷却，冷却效果可以通过装备补充，选择标准的攻速铭文可以让扁鹊轻松普攻叠加被动到5层，达到最大收益，穿透效果可以帮助扁鹊在前期达到伤害最大化，移速可以增加扁鹊gank支援能力。');",
                "INSERT INTO `hero_inscription` VALUES ('120', '1512,3503,2501', '白起作为一名坦克，红色宿命可以提升血量和物理防御能力；蓝色推荐长生，可以增加白起的坦克能力；绿色推荐均衡可以提升白起的双抗能力。');",
                "INSERT INTO `hero_inscription` VALUES ('121', '1514,3516,2520', '红色梦魇提供了法术攻击和法术穿透的加成，可以在前期让芈月有非常高的伤害，芈月的持续伤害源于技能和被动，因此选择可以增加攻速的铭文狩猎。绿色选择怜悯，减少芈月技能冷却的时间。');",
                "INSERT INTO `hero_inscription` VALUES ('123', '1520,3509,2517', '吕布作为一名近战英雄，物理攻击及生存能力至关重要，移速的增加能提高吕布的支援速度，在战场上以更灵活的身姿作战，6%的冷却缩减可让吕布更频繁地使用技能去打出更多的伤害');",
                "INSERT INTO `hero_inscription` VALUES ('124', '1514,3511,2503', '周瑜作为一名法师，符文主要补充法术穿透和法术吸血，高级符文补充一些冷却缩短');",
                "INSERT INTO `hero_inscription` VALUES ('125', '1519,3514,2506', '输出较为安全的元歌主要以暴击、暴击效果为主，争取打出爆发伤害压制对手');",
                "INSERT INTO `hero_inscription` VALUES ('126', '1512,3514,2515', '夏侯惇是一个典型的坦克英雄，红色宿命提供攻速和生命物理防御；蓝色铭文调和补充移速和生命回血，更加方便于支援游走；同时绿色鹰眼补足一些伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('127', '1514,3516,2512', '这套铭文提供法术强度、法术穿透、冷却减缩，以及法术吸血。法术穿透和法术强度增加甄姬的输出能力，法术吸血让甄姬有一定的续航能力，冷却减缩减少技能CD，让甄姬能更快得打出技能被动冰冻敌方并造成伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('128', '1512,3514,2520', '曹操作为一名战士，宿命提高突进时的生存能力，鹰眼保证在切入后排时能有足够的伤害击杀脆皮，狩猎提供的移动速度提高机动性。');",
                "INSERT INTO `hero_inscription` VALUES ('129', '1503,3509,2517', '典韦是后期英雄，输出更依赖于大招的真实伤害，传承可以让他造成更多真实伤害，隐匿移速提升配合1技能可以黏住敌方核心输出英雄，虚空提升一定的坦克能力。');",
                "INSERT INTO `hero_inscription` VALUES ('130', '1504,3514,2520', '宫本的玩法是在poke时寻找时机切入并击败敌方的核心输出，宫本比较依赖普攻，同时技能后可以为普攻增加海量物理伤害加成，百穿铭文可以帮助宫本伤害收益最大化，让宫本可以更好的利用技能配合普攻产生高额伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('131', '1504,3514,2517', '这套铭文可以为李白提供物理攻击能力和百穿效果，能确保李白在初期就可以配合2技能减少抗性把大招伤害完美呈现，虽然解封大招是李白的关键玩法，但是李白最常用是野区累积解封或者1技能后平A组合技能解封。');",
                "INSERT INTO `hero_inscription` VALUES ('132', '1520,3514,2520', '马可波罗的攻击速度会影响技能子弹数量，需要优先搭配提升');",
                "INSERT INTO `hero_inscription` VALUES ('133', '1505,3514,2520', '纷争的物理吸血属性可以提高狄仁杰前期的续航能力，鹰眼可以大幅度提高狄仁杰在前期的物理输出，狩猎则可以额外强化狄仁杰的攻速和移速，配合被动的移速加成，可以让自身移动能力接近于一般刺客，增加生存能力。');",
                "INSERT INTO `hero_inscription` VALUES ('134', '1504,3514,2520', '这套铭文增加物理攻击、物理穿透、移速与攻击速度。物理攻击与穿透的加成增加技能和普攻的伤害，配合技能大幅度削弱敌人的护甲和防御效果，移速加成提高机动性，攻速加成让释放技能后能更快速打出被动的额外伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('135', '1512,3509,2515', '这是一套标准的肉坦铭文，调和提升他的抗肉和续航能力。宿命提供的攻击速度可以让他打出更多的普攻，虚空提供的冷却缩减大幅度降低技能CD。');",
                "INSERT INTO `hero_inscription` VALUES ('136', '1514,3511,2512', '一定的冷却缩减可以保证技能持续输出，法强和法穿在前中期能保证一定的伤害量，法术吸血保证续航能力，在线上依赖被动回蓝，技能吸血来保证发育。');",
                "INSERT INTO `hero_inscription` VALUES ('139', '1520,3514,2520', '这套铭文其中物理攻击和物理穿透可以提高老夫子的伤害，暴击率搭配老夫子的高攻击速度表现非常的不俗，移动速度加成提升老夫子支援和gank速度，攻击速度的加成让普攻速度更快，在触发被动效果后能够打出更高的伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('140', '1504,3514,2517', '异变和鹰眼的百穿效果再加上异变和隐匿的36的物理攻击，能够让关羽的每一刀都特别痛。而隐匿的10%的移速能够让这匹马跑得更快，追杀或者逃跑都很方便。');",
                "INSERT INTO `hero_inscription` VALUES ('141', '1514,3516,2512', '法术攻击和穿透可以在初期保障伤害，应对敌方换血和快速清线，吸血提供的恢复能力可以增加貂蝉的赖线能力；貂蝉对冷却缩减的效果非常依赖，符文选择带10％冷却，在初期配合回血书就有一定的作战能力。');",
                "INSERT INTO `hero_inscription` VALUES ('142', '1501,3511,2512', '安琪拉是一个高爆发的法师英雄，操作简单，技能的法术强度收益超高是她的特色，三个铭文都可以为安琪拉提供法术强度收益是最好的选择，轮回的法术吸血也可以提升安琪拉的前中期续航能力。');",
                "INSERT INTO `hero_inscription` VALUES ('144', '1504,3503,2517', '程咬金定位是战士和坦克，既坦克承受伤害的能力又有输出能力；异变可以为程咬金提升一些攻击力和穿透能力，蓝色隐匿可以提供攻击力和移速，方便黏人输出，绿色均衡提升双抗可以让程咬金承受更多伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('146', '1517,3515,2520', '露娜的技能需要配合普攻才能多次刷新大招，攻速加成可以提升露娜的攻击频率，移速加成可以提升支援gank的能力，法穿可以增加露娜的伤害能力，冷却可以减少技能CD。');",
                "INSERT INTO `hero_inscription` VALUES ('148', '1514,3511,2512', '红色梦魇提供的法术攻击和法术穿透能有效提高前期的输出，绿色献祭提高输出频率增加输出伤害，蓝色轮回能在后期高伤害的情况下获得一定的续航能力。');",
                "INSERT INTO `hero_inscription` VALUES ('149', '1512,3509,2501', '宿命提供的属性可以让刘邦更好的为队友吸收伤害，攻速加成可以让刘邦打出更多的普攻配合被动技能打出更多伤害，技能冷却可以让刘邦有机会放出更多次技能。');",
                "INSERT INTO `hero_inscription` VALUES ('150', '1505,3514,2506', '红色纷争，物理攻击可以让韩信足够的输出，提升清野效率和与敌方的对抗能力；绿色鹰眼，物理穿透进一步提高韩信的穿甲输出能力；蓝色兽痕，暴击率的加成可以在一定程度上使得韩信的输出最大化。');",
                "INSERT INTO `hero_inscription` VALUES ('152', '1501,3516,2512', '法术攻击的叠加，提高昭君带线能力，前期较有优势，达到四级时就可以带走敌方中单，或者边路支援打野抓人。CD的缩减主要针对后期，可以使昭君技能频繁使用，进行消耗。吸血效果可以很好的提升线上续航能力。');",
                "INSERT INTO `hero_inscription` VALUES ('153', '1504,3514,2517', '兰陵王是刺客，依赖技能短期高爆发，输出节奏为阶梯式；最大化收益属性就是破甲和物理伤害，移动。；红色选择异变，提供破甲＋物理伤害；绿色鹰眼，提供破甲＋物理伤害；蓝色隐匿，提供高额物理输出＋机动性。');",
                "INSERT INTO `hero_inscription` VALUES ('154', '1504,3514,2517', '花木兰的铭文根据玩法不同有很多变化，推荐百穿玩法，攻击力和穿透可以提升花木兰的普攻和技能伤害，移速提升可以帮助花木兰支援队友，对于新手来说可以选择这套铭文轻松打出有效伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('156', '1514,3511,2512', '法功和法术穿透可以强化张良前期作战能力，法术吸血可以增强续航能力，张良的被动依赖对敌人持续的控制和伤害来触发，更短的冷却时间对此很有帮助');",
                "INSERT INTO `hero_inscription` VALUES ('157', '1514,3516,2512', '此铭文法术攻击加成能让火舞在前中期就打出一定的伤害量，同时火舞没有蓝条的限制，一定的吸血铭文可以在很大程度上减少其回家补给的次数，冷却缩减可以更好的消耗敌方，同时再配上其被动以达到最优输出或是撤离战场。');",
                "INSERT INTO `hero_inscription` VALUES ('162', '1504,3514,2517', '鹰眼和异变提供的攻击力及穿透可让娜可露露造成大量伤害，大招配合隐匿的移速使她更快的加入战局');",
                "INSERT INTO `hero_inscription` VALUES ('163', '1504,3514,2517', '橘右京是一个依赖技能伤害的英雄，需要高额的穿透能力打足伤害，移速的提升可以帮助橘右京配合被动减速技能风筝敌方英雄，物理伤害的提升可以帮橘右京提升初期的清线能力。');",
                "INSERT INTO `hero_inscription` VALUES ('166', '1512,3509,2515', '红色宿命增加血量，物理护甲以提高前期抗压能力。绿色虚空增加血量，降低技能CD使亚瑟的技能更加频繁。蓝色调和增加血量，增加移速，可以使亚瑟更加灵活。');",
                "INSERT INTO `hero_inscription` VALUES ('167', '1510,3514,2506', '孙悟空的被动是自带暴击率，无双与兽痕可以增加13％的暴击率，使得初始暴击率能达到33％。通过无双的弥补，使得初始暴击效果为186％，使得输出更高。鹰眼提供物理穿透可以增加孙悟空的穿透效果，增加伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('168', '1512,3503,2501', '牛魔作为一个坦克、辅助位英雄，在团战中需要让自身拥有更高的血量及护甲魔抗才能为队友抵抗敌方的输出。');",
                "INSERT INTO `hero_inscription` VALUES ('169', '1520,3514,2517', '后羿是一个高爆发型的射手，非常依赖普攻效果，攻速，暴击，移速，对于后羿都是非常关键的属性');",
                "INSERT INTO `hero_inscription` VALUES ('170', '1504,3514,2504', '刘备是近战战士，距离敌人越近攻击伤害越高，高额的穿透和物攻可以帮刘备飞速清理野区，移速可以让刘备配合红BUFF黏住敌方英雄，在初期爆发超高的情况下，攻速效果明显，近战时多A一下可以造成更多伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('171', '1512,3509,2515', '张飞是坦克，蓝色调和提升最大生命、回血；绿色虚空提供的冷却缩减可以更好多释放技能；红色宿命为变身后输出作保障，最大生命、物理防御还能增加张飞的肉度。');",
                "INSERT INTO `hero_inscription` VALUES ('173', '1520,3514,2517', '隐匿和红月提升物理攻击，鹰眼的物理穿透能够让李元芳技能伤害更高。隐匿的10%的移速可以帮李元芳在初期更好的gank，是追杀与逃生的利器。');",
                "INSERT INTO `hero_inscription` VALUES ('174', '1504,3514,2520', '虞姬的伤害来源都来自于技能和被动，所以想在前期就能造成很高的伤害，物理攻击和物理穿透都是非常必要的；攻速和移速也是一个ADC非常关注的属性，所以这套铭文非常适合虞姬这类靠技能的ad英雄。');",
                "INSERT INTO `hero_inscription` VALUES ('175', '1514,3516,2512', '这套铭文达到了10%的减CD以及较高的法术强度和法术穿透。保证了钟馗的输出能力，配合肉装和输出冷却符文，能够将技能的控制频率提到最高，这一点也保证了钟馗玩法的最大化收益。');",
                "INSERT INTO `hero_inscription` VALUES ('176', '1501,3511,2512', '在铭文选择上，更加偏向选择增加正面作战能力的属性：法术攻击与法术吸血，搭配冷却铭文可以有效的缩短技能空窗期');",
                "INSERT INTO `hero_inscription` VALUES ('177', '1519,3514,2520', '成吉思汗最大的优势在草丛作战的全面性，无论是移速，视野提供都尤为出色，狩猎的移速和攻速可以提升机动性，穿透效果可以确保前中期的伤害足够，红色暴击是博伤害的玩法，前中期一旦出现暴击可以达到非常高的收益。');",
                "INSERT INTO `hero_inscription` VALUES ('178', '1504,3514,2520', '这套铭文的属性伤害很高。物理穿透保证了他的伤害。10%的移速加成则使他在游走gank和团战支援时更加迅速。10%的攻速加成则是二技能普攻会带有真实伤害。');",
                "INSERT INTO `hero_inscription` VALUES ('179', '1514,3511,2512', '放弃一些对前排的威胁，保持了持续作战的能力，但利用女娲大招的特性，依然可以对后排造成不小的威胁');",
                "INSERT INTO `hero_inscription` VALUES ('180', '1512,3509,2517', '宿命提供的攻速、生命、物防完美契合哪吒肉核冲锋的定位，虚空一方面提供了哪吒肉的属性，另一方面让其在战场中释放更多技能来帮助团队，隐匿能够让哪吒更灵活。');",
                "INSERT INTO `hero_inscription` VALUES ('182', '1514,3516,2512', '干将莫邪拥有超远距离输出的能力，选择铭文以增加输出能力为主。这套铭文能提供66点法术攻击，24点法术穿透，10％法术吸血，10％冷却缩减。66点法术攻击和24点法术穿透为干将莫邪前期提供了一定的伤害基础。');",
                "INSERT INTO `hero_inscription` VALUES ('183', '1504,3514,2520', '红色选择异变，绿色选择鹰眼，增加物理攻击力和物理穿透，可以最大程度增加雅典娜前期伤害，提高发育效率。蓝色选择狩猎，增加10%攻速和10%移速，提高攻速可以帮助雅典娜的连招打的更加流畅。');",
                "INSERT INTO `hero_inscription` VALUES ('184', '1501,3503,2501', '蔡文姬是一个辅助英雄，红色法术强度可以提升主动技能和被动技能的恢复能力，长生可以提升坦克强度，在初期中期很难被击杀，在前期可以多次触发被动创造丝血逃生的机会，均衡可以提升双抗让蔡文姬放心为队友恢复血量。');",
                "INSERT INTO `hero_inscription` VALUES ('186', '1514,3511,2515', '梦魇增加了太乙真人前期的输出能力，绿色献祭在增加了太乙真人的法术攻击外，还提供了冷却缩减；蓝色调和增加了太乙真人在前期的续航能力，同时增加gank能力。');",
                "INSERT INTO `hero_inscription` VALUES ('187', '1512,3509,2515', '东皇太一的玩法是游走和大招以血还血，这套铭文可以在初期为东皇太一增加移速、生命值、恢复能力，可以提升初期游走和gank能力，中后期至少可以大到一个核心。');",
                "INSERT INTO `hero_inscription` VALUES ('189', '1514,3511,2520', '鬼谷子的技能有不俗的伤害，法术攻击和法术穿透可以保障初期gank时的伤害能力，狩猎可以增加鬼谷子支援效率和伤害，献祭减少技能冷却，让鬼谷子隐身频率更高。');",
                "INSERT INTO `hero_inscription` VALUES ('190', '1514,3516,2512', '诸葛亮作为强力法师，红色梦魇提升法术攻击、法术穿透，保证打出强力伤害。绿色怜悯提升技能冷却，保证可以多放技能、多打出被动，对敌人造成高额伤害。蓝色轮回提升法术吸血能力，在对战中保证生存能力以及续航能力。');",
                "INSERT INTO `hero_inscription` VALUES ('191', '1514,3516,2512', '大乔是辅助，四个技能两个控制伤害两个传送技能，大乔除了2技能外均有较高的法术伤害及可观的法术加成。大乔的四个技能cd都比较长，使用怜悯可以对冷却进行一定的缩减加成，梦魇和轮回是相对收益比较高的法术铭文。');",
                "INSERT INTO `hero_inscription` VALUES ('192', '1510,3514,2506', '黄忠作为站桩输出，对移速没有需求，前期有1技能加速和2技能减速敌方需求也不大，因此主攻击和穿透，中后期无双的暴击效果配合无尽可以呈现出毁灭程度的攻击力。');",
                "INSERT INTO `hero_inscription` VALUES ('193', '1503,3514,2517', '铠的一技能回复能力与物理加成有关，传承增加物理攻击力。鹰眼所提供的物理穿透提升后期伤害。隐匿不仅可以提高铠的物理攻击力，还可以增加铠的移速更好的追击敌人。');",
                "INSERT INTO `hero_inscription` VALUES ('194', '1504,3509,2517', '因为苏烈整体的防御能力较强，所以符文推荐以攻击和移速为主，达到攻守平衡。');",
                "INSERT INTO `hero_inscription` VALUES ('195', '1504,3514,2517', '玄策应该优先选择穿透铭文搭配暗影战斧，针对后排进行突进输出');",
                "INSERT INTO `hero_inscription` VALUES ('196', '1504,3514,2517', '物理攻击和物理穿透提升前期输出能力，移动速度保证游走逃脱能力');",
                "INSERT INTO `hero_inscription` VALUES ('197', '1514,3511,2520', '作为中单法师，法术穿透和法术攻击能很好的强化其输出能力，冷却缩减可以使弈星获得更多的棋子，普通攻击是法术伤害，加持一定的攻速后，可以给予敌人造成巨大的威胁。');",
                "INSERT INTO `hero_inscription` VALUES ('198', '1514,3509,2501', '因为梦奇自身具有不错的双抗加成，所以符文推荐生命值和法术攻击为主，更有效的提升生存能力并弥补输出');",
                "INSERT INTO `hero_inscription` VALUES ('199', '1503,3514,2517', '阿离的被动，对于攻击速度的需求较低，优先物理攻击，移动速度依然是不可缺少的特殊属性；通过物理穿透可强化阿离的前期作战能力，使得阿离在前期也会有一定的威胁能力');",
                "INSERT INTO `hero_inscription` VALUES ('501', '1514,3515,2520', '明世隐前期较为强势，铭文也将偏向增加其对线期的伤害能力，移速铭文能保持连接不被轻易拉开');",
                "INSERT INTO `hero_inscription` VALUES ('502', '1504,3514,2517', '裴擒虎前期选择物理穿透会让其在前期无视大部分英雄的自身护甲，移动速度和攻击速度会大幅优化裴擒虎的清野效率');",
                "INSERT INTO `hero_inscription` VALUES ('503', '1512,3509,2520', '铭文补充狂铁一定的最大生命值和攻速，冷却缩减，能够补充其前期对抗能力，线上更加灵活');",
                "INSERT INTO `hero_inscription` VALUES ('504', '1514,3511,2512', '米莱狄属于推进型法师，不必太在意移动速度，主要还是补充线上的续航和战斗属性');"};
        Cursor cursor1 = db.rawQuery("select * from hero ",null);
        if(cursor1.getCount()==0)
        {
            for(int i =0;i<insert.length;i++){
                db.execSQL(insert[i]);
                cursor1.close();
            }
        }
        cursor1.close();
        Cursor cursor2 = db.rawQuery("select * from skill ",null);
        if(cursor2.getCount()==0)
        {
            for(int i =0;i<insertSkill.length;i++){
                db.execSQL(insertSkill[i]);
                cursor2.close();
            }
        }
        cursor2.close();
        Cursor cursor3 = db.rawQuery("select * from hero_equip ",null);
        if(cursor3.getCount()==0)
        {
            for(int i =0;i<insertEquip.length;i++){
                db.execSQL(insertEquip[i]);
                cursor3.close();
            }
        }
        cursor3.close();
        Cursor cursor4 = db.rawQuery("select * from hero_inscription ",null);
        if(cursor4.getCount()==0)
        {
            for(int i =0;i<insertInscription.length;i++){
                db.execSQL(insertInscription[i]);
                cursor4.close();
            }
        }
        cursor4.close();
        db.close();
    }

    public void initEquipment(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(EQUIPMENT_TABLE, null, null, null,null,null,null);
        if(c.getCount()!=0){
            db.close();
            c.close();
            return;
        }

        db.execSQL( "INSERT INTO `equip` VALUES (1111, '铁剑', 1, 150, 250, '+20物理攻击', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1111.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1112, '匕首', 1, 174, 290, '+10%攻击速度 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1112.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1113, '搏击拳套', 1, 192, 320, '+8%暴击率 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1113.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1114, '吸血之镰', 1, 246, 410, '+10物理攻击 +8%物理吸血', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1114.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1116, '雷鸣刃', 1, 270, 450, '+40物理攻击', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1116.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1117, '冲能拳套', 1, 330, 550, '+15%暴击率 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1117.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1121, '风暴巨剑', 1, 546, 910, '+80物理攻击 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1121.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1122, '日冕', 1, 474, 790, '+40物理攻击 +300最大生命 ', '唯一被动-残废：普通攻击有30%几率降低敌人20%移动速度，持续2秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1122.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1123, '狂暴双刃', 1, 534, 890, '+15%攻击速度 +10%暴击率 +5%移速', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1123.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1124, '陨星', 1, 250, 1080, '+45物理攻击 +10%冷却缩减', '唯一被动-切割：+60护甲穿透', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1124.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1125, '破魔刀', 1, 910, 2000, '物理攻击+100 +50法术防御', '唯一被动-破魔：增加等同于自身物理攻击40%的法术防御，最多增加300点', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1125.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1126, '末世', 1, 1296, 2160, '+60物理攻击 +30%攻击速度  +10%物理吸血', '唯一被动-破败：普通攻击附带敌人当前生命值8%的物理伤害（对野怪最多：80）', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1126.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1127, '名刀?司命', 1, 1056, 1760, '+60物理攻击 +5%冷却缩减', '唯一被动-暗幕：免疫致命伤并免疫伤害、增加20%移动速度持续1秒近战/0.5秒远程，90秒冷却', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1127.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1128, '冰霜长矛', 1, 1182, 1980, '+80物理攻击 +600最大生命 ', '唯一被动-碎冰：普通攻击会减少目标30%攻击速度和移动速度，持续2秒 远程英雄使用时减速效果持续时间衰减为1秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1128.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1129, '速击之枪', 1, 534, 890, '+25%攻击速度', '唯一被动-精准：普通攻击伤害提升30点，远程英雄使用时该效果翻倍。', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1129.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1131, '碎星锤', 1, 1260, 2100, '+80物理攻击 +10%冷却缩减', '唯一被动：+45%物理护甲穿透', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1131.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1132, '泣血之刃', 1, 1044, 1740, '+100物理攻击 +25%物理吸血 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1132.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1133, '无尽战刃', 1, 1284, 2140, '+120物理攻击 +20%暴击率', '唯一被动：+50%暴击效果', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1133.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1134, '宗师之力', 1, 1506, 2100, '+60物理攻击 +20%暴击率 +400 最大法力 +400最大生命', '唯一被动-强击：使用技能后，2秒内提升自身8%移动速度，并使得下次普通攻击造成额外1.0*物理攻击的物理伤害，冷却时间：2秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1134.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1135, '闪电匕首', 1, 1104, 1840, '+30%攻击速度 +20%暴击率 +8%移速', '唯一被动-电弧：普通攻击有30%几率释放连锁闪电，对目标造成（100+0.3AD）点法术伤害（该效果有0.5秒CD），这个伤害可以暴击', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1135.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1136, '影刃', 1, 1242, 2070, '+40%攻击速度 +20%暴击率 +5%移速', '唯一被动-暴风：暴击后提升自身30%攻击速度和10%移动速度，持续2秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1136.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1137, '暗影战斧', 1, 1254, 2090, '+85物理攻击 +15%冷却缩减 +500最大生命', '唯一被动-切割：增加(50+英雄等级*10)点护甲穿透 唯一被动-残废：普通攻击有30%几率降低敌人20%移动速度，持续2秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1137.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1138, '破军', 1, 1770, 2950, '+200物理攻击 ', '唯一被动-破军：目标生命低于50%时伤害提高30%', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1138.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1154, '穿云弓', 1, 660, 1100, '物理攻击+40 攻击速度+10%', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1154.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1155, '破晓', 1, 2040, 3400, '物理攻击+50 攻击速度+35% 暴击率+15%', '唯一被动-破甲：+22.5%物理穿透（远程英雄使用时效果翻倍） 唯一被动：普通攻击伤害提升50点（远程英雄使用时效果翻倍）', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1155.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1211, '咒术典籍', 2, 180, 300, '+40法术攻击 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1211.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1212, '蓝宝石', 2, 132, 220, '+300最大法力', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1212.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1213, '炼金护符', 2, 72, 120, '+10 每5秒回蓝 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1213.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1214, '圣者法典', 2, 300, 500, '+20法术攻击 +8%冷却缩减 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1214.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1216, '元素杖', 2, 324, 540, '+80法术攻击 ', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1216.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1221, '大棒', 2, 492, 820, '+120法术攻击', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1221.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1222, '血族之书', 2, 744, 1240, '+75法术攻击 +10%冷却缩减', '唯一被动-嗜血：增加20%法术吸血', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1222.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1223, '光辉之剑', 2, 468, 730, '+400最大生命 +400最大法力 ', '唯一被动-强击：使用技能后，5秒内的下一次普通攻击附加50%物理攻击（+30%法术加成）的法术伤害，这个效果有2秒的冷却时间', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1223.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1224, '魅影面罩', 2, 612, 1020, '+70法术攻击 +300最大生命', '唯一被动：+75法术穿透', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1224.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1225, '进化水晶', 2, 432, 720, '+400最大法力 +400最大生命', '唯一被动-英勇奖赏：升级后在3秒内回复20%生命与法力值', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1225.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1226, '圣杯', 2, 1218, 1930, '+180法术攻击 +15%冷却缩减 +25每5秒回蓝', '唯一被动-法力源泉：每5秒恢复5%法力值', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1226.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1227, '炽热支配者', 2, 1170, 1950, '+180法术攻击 +600最大法力 +15 每5秒回蓝', '唯一被动-法力护盾：生命值低于30%时，立刻获得一个吸收450-1500（+50%法术加成）伤害的护盾并提升30%移动速度，持续4秒，这个效果有90秒冷却时间', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1227.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1229, '破碎圣杯', 2, 540, 900, '+80法术攻击 +5%冷却缩减 +20 每5秒回蓝', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1229.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1231, '虚无法杖', 2, 1266, 2110, '+180法术攻击 +500最大生命值', '唯一被动：+45%法术穿透', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1231.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1232, '博学者之怒', 2, 1380, 2300, '+240法术攻击', '唯一主动-毁灭：法术攻击提升35%', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1232.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1233, '回响之杖', 2, 1260, 2100, '+240法术攻击  +7%移速 ', '唯一被动-回响：技能命中会触发小范围爆炸造成50（+50%法术加成）法术伤害，这个效果有5秒CD', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1233.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1234, '冰霜法杖', 2, 1260, 2100, '+150法术攻击  +1050最大生命', '唯一被动-结霜：英雄技能造成伤害会附带20%的减速效果，持续2秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1234.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1235, '痛苦面具', 2, 1224, 2040, '+140法术攻击 +5%冷却缩减 +500最大生命', '唯一被动-折磨：技能伤害会造成相当于目标当前生命值8%的法术伤害，这个效果有3秒CD（对野怪伤害上限：200） 唯一被动：+75法术穿透', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1235.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1236, '巫术法杖', 2, 1272, 2120, '+140法术攻击 +400最大生命 +400最大法力 +8%移速', '唯一被动-强击：使用技能后，5秒内下一次普通攻击附加30%物理攻击（+80%法术加成）的法术伤害，这个效果有2秒冷却时间', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1236.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1237, '时之预言', 2, 1254, 2090, '+160法术攻击 +600最大法力 +800最大生命', '唯一被动-英勇奖赏：升级后在3秒内回复20%生命值与法力值 唯一被动-守护：每5点法功提升1点物理和法术防御，最多提升200点', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1237.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1238, '贤者之书', 2, 1794, 2990, '+400法术攻击', '唯一被动-刻印：增加1400点生命', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1238.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1239, '辉月', 2, 1194, 1990, '+160法术攻击 +10%冷却缩减', '唯一主动-月之守护：90秒CD，免疫所有效果，不能移动、攻击和使用技能，持续1.5秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1239.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1240, '噬神之书', 2, 1254, 2090, '+180法术攻击 +10%冷却缩减 +800最大生命', '唯一被动-嗜血：增加25%法术吸血', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1240.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1311, '红玛瑙', 3, 180, 300, '+300最大生命', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1311.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1312, '布甲', 3, 132, 220, '+90物理防御', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1312.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1313, '抗魔披风', 3, 132, 220, '+90法术防御', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1313.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1314, '提神水晶', 3, 84, 140, '+30 每5秒回复', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1314.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1321, '力量腰带', 3, 540, 900, '+1000最大生命', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1321.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1322, '熔炼之心', 3, 540, 900, '+700最大生命', '唯一被动-献祭：每秒对身边的敌军造成（60+英雄等级*2）点法术伤害', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1322.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1323, '神隐斗篷', 3, 612, 1020, '+120法术防御 +700最大生命 +50每5秒回血', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1323.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1324, '雪山圆盾', 3, 540, 900, '+10%减CD +400最大法力 +110物理防御', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1324.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1325, '守护者之铠', 3, 438, 730, '+210物理防御', '唯一被动-寒铁：受到攻击会减少攻击者15%攻击速度，持续3秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1325.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1327, '反伤刺甲', 3, 882, 1840, '+40物理攻击 +420物理防御', '唯一被动-荆棘：受到物理伤害时，会将伤害量的20%以法术伤害的形式回敬给对方。自身每20点物理防御属性提升1%该伤害（最多+100%）。攻击者距离越远，这个伤害越低，最多在距离800时衰减至70%', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1327.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1328, '血魔之怒', 3, 1272, 2120, '+20物理攻击 +1000最大生命', '唯一被动-血怒：生命值低于30%时获得血怒，增加80点攻击，并获得最大生命值30%的护盾，持续8秒，这个效果有90秒CD', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1328.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1331, '红莲斗篷', 3, 1098, 1830, '+240物理防御 +1000最大生命', '唯一被动-献祭：每秒对身边300范围内的敌人造成使用者最大生命值2%的法术伤害', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1331.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1332, '霸者重装', 3, 1422, 2070, '+2000最大生命', '唯一被动-复苏：脱离战斗后每秒回复3%最大生命值', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1332.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1333, '不祥征兆', 3, 1308, 2180, '+270物理防御 +1200最大生命', '唯一被动-寒铁：受到攻击会减少攻击者30%攻击速度与15%移动速度，持续3秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1333.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1334, '不死鸟之眼', 3, 1260, 2100, '+100每5秒回血 +240法术防御 +1200最大生命', '唯一被动-血统：每损失10%生命值，受到的所有治疗效果会额外增加6%。', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1334.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1335, '魔女斗篷', 3, 1272, 2120, '+360法术防御 +1000最大生命', '唯一被动-迷雾：脱战3秒后获得一个吸收（200+英雄等级*120）点法术伤害的护盾', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1335.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1336, '极寒风暴', 3, 1260, 2100, '+20%冷却缩减 +500最大法力 +360物理防御', '唯一被动-冰心：受到单次伤害超过当前生命值10%时触发寒冰冲击，对周围敌人造成（50+英雄等级*10）点法术伤害并降低其30%攻击和移动速度，持续2秒，这个效果有2秒内置CD', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1336.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1337, '贤者的庇护', 3, 1248, 2080, '+140物理防御 +140法术防御', '唯一被动-复生：死亡后2秒原地复活，并获得（2000+英雄等级*100）点生命值，冷却时间：150秒。这个效果每局游戏只能触发2次', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1337.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1338, '暴烈之甲', 3, 1092, 1950, '+220物理防御 +1000最大生命', '唯一被动-无畏：每次受到伤害后，自身造成的所有伤害提升2%并增加2%的移速，这个效果最高可以叠加5层，最多提升10%的伤害输出和10%移速，持续3秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1338.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1411, '神速之靴', 4, 150, 250, '唯一被动：+30移动速度', '所有鞋类装备的移速加成效果不叠加', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1411.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1421, '影忍之足', 4, 414, 710, '+110物理防御', '唯一被动：+60移动速度（所有鞋类装备的移速加成效果不叠加） 唯一被动：减少15%受到普通攻击的伤害', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1421.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1422, '抵抗之靴', 4, 414, 710, '+110法术防御', '唯一被动：+60移动速度（所有鞋类装备的移速加成效果不叠加） 唯一被动：+35%韧性', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1422.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1423, '冷静之靴', 4, 426, 710, '+15%减CD', '唯一被动-静谧：所有英雄技能的冷却恢复速度加快3%~10%（随英雄等级成长）这个效果对剩余冷却时间小于5秒的技能不会生效。 唯一被动：+60移动速度（所有鞋类装备的移速加成效果不叠加）', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1423.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1424, '秘法之靴', 4, 474, 710, '唯一被动：+60移动速度（所有鞋类装备的移速加成效果不叠加） 唯一被动：+75法术穿透', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1424.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1425, '急速战靴', 4, 426, 710, '+30%攻速', '唯一被动：+60移动速度（所有鞋类装备的移速加成效果不叠加）', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1425.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1426, '疾步之靴', 4, 378, 530, '唯一被动-神行：脱离战斗后增加60移动速度 唯一被动：+60移动速度（所有鞋类装备的移速加成效果不叠加）', NULL, 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1426.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1511, '狩猎宽刃', 5, 150, 250, '（打野刀升级后将惩戒技能替换为寒冰惩戒：寒冰惩戒可对英雄使用，造成伤害和减速效果） 必须携带惩击才可够买', '必须被动唯一被动-打野：增加35%对野怪的伤害，击杀野怪获得经验提升20%', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1511.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1521, '游击弯刀', 5, 450, 750, '必须携带惩击才可够买，获得寒冰惩击效果', '唯一被动-打野：增加45%对野怪的伤害，击杀野怪获得经验提升30%，击杀野怪获得的金币提升20% 唯一被动-磨砺：击杀野怪增加自身6点法术攻击，最多叠加20层', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1521.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1522, '巡守利斧', 5, 450, 750, '必须携带惩击才可够买，获得寒冰惩击效果', '唯一被动-打野：增加45%对野怪的伤害，击杀野怪获得经验提升30%，击杀野怪获得的金币提升20% 唯一被动-磨砺：击杀野怪增加自身50点最大生命，最多叠加20层', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1522.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1523, '追击刀锋', 5, 450, 750, '必须携带惩击才可够买，获得寒冰惩击效果', '唯一被动-打野：增加45%对野怪的伤害，击杀野怪获得经验提升30%，击杀野怪获得的金币提升20% 唯一被动-磨砺：击杀野怪增加自身2点物理攻击和0.5%冷却缩减，最多叠加20层', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1523.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1531, '符文大剑', 5, 894, 1490, '+100法术攻击 +400最大法力 必须携带惩击才可够买，获得寒冰惩击效果', '唯一被动-强击：使用技能后，5秒内的下一次普通攻击附加50+0.5*法术攻击的法术伤害，冷却时间：2秒 唯一被动-打野：增加45%对野怪的伤害，击杀野怪获得经验提升30%，击杀野怪获得的金币提升20% 唯一被动-磨砺：击杀野怪增加自身6点法术攻击，最多叠加30层', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1531.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1532, '巨人之握', 5, 900, 1500, '+800最大生命 必须携带惩击才可够买，获得寒冰惩击效果', '唯一被动-献祭：每秒对身边300范围内的敌人造成（30+英雄等级*6）点法术伤害 唯一被动-打野：增加45%对野怪的伤害，击杀野怪获得经验提升30%，击杀野怪获得的金币提升20% 唯一被动-磨砺：击杀野怪增加自身50点最大生命，最多叠加30层', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1532.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1533, '贪婪之噬', 5, 876, 1460, '+40物理攻击 +15%攻击速度 +8%移速 必须携带惩击才可够买，获得寒冰惩击效果', '唯一被动-打野：增加45%对野怪的伤害，击杀野怪获得经验提升30%，击杀野怪获得的金币提升20% 唯一被动-磨砺：击杀野怪增加自身2点物理攻击和0.5%冷却缩减，最多叠加30层', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1533.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1701, '学识宝石', 7, 180, 300, '+移动速度+5%', '唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1701.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1711, '凤鸣指环', 7, 642, 1070, '+移动速度+5% +生命值500', '所有辅助装备的主动技能均为全队共享冷却时间 唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一主动-鼓舞：45秒CD，为周围友方英雄增加30%攻击速度和10%冷却缩减，持续5秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1711.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1712, '风之轻语', 7, 606, 1010, '+移动速度+5% +生命值500', '所有辅助装备的主动技能均为全队共享冷却时间 唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一主动-救援：60秒CD，立即为周围血量最低的友方英雄（包括自己）提供一个吸收（500+英雄等级*50）伤害的护盾，并提升其15%移动速度，持续3秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1712.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1713, '风灵纹章', 7, 708, 1180, '+移动速度+5% +生命值500', '所有辅助装备的主动技能均为全队共享冷却时间 唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一主动-奔腾号令：60秒CD，增加周围所有友方英雄30%的移动速度，持续3秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1713.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1714, '鼓舞之盾', 7, 726, 1210, '+移动速度+5% +生命值500', '唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一被动-军团：提升周围800范围友军(20+英雄等级*2)点物理攻击和(40+英雄等级*4)点法术攻击', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1714.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1721, '极影', 7, 1146, 1910, '+移动速度+10% +生命值1000', '所有辅助装备的主动技能均为全队共享冷却时间 唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一主动-鼓舞：45秒CD，为周围友方英雄增加50%攻击速度和20%冷却缩减，持续5秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1721.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1722, '救赎之翼', 7, 1080, 1800, '+移动速度+10% +生命值1000', '所有辅助装备的主动技能均为全队共享冷却时间 唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一主动-救援：60秒CD，立即为周围血量最低的友方英雄（包括自己）提供一个吸收（800+英雄等级*80）伤害的护盾，并提升其30%移动速度，持续3秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1722.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1723, '奔狼纹章', 7, 1254, 2090, '+移动速度+10% +生命值1000', '所有辅助装备的主动技能均为全队共享冷却时间 唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一主动-奔腾号令：60秒CD，增加周围所有友方英雄50%的移动速度，持续3秒', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1723.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (1724, '近卫荣耀', 7, 1206, 2010, '+移动速度+10% +生命值1000', '唯一被动-奉献：如果自己的经验或经济是己方最低，每3秒会额外获得5点经验或金币 唯一被动-军团：提升周围800范围友军(30+英雄等级*3)点物理攻击和(60+英雄等级*6)点法术攻击', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/1724.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (11210, '制裁之刃', 1, 960, 1800, '+100物理攻击 +10%物理吸血 \n', '唯一被动-重伤：造成伤害使得目标的生命恢复效果减少50%，持续1.5秒（如果该伤害由普攻触发，则持续时间延长至3秒）', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/11210.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (11311, '纯净苍穹', 1, 1338, 2230, '+40%攻击速度 +20%暴击率', '唯一被动-精准：普通攻击伤害提升35点，远程英雄使用时该效果翻倍。 \n唯一主动-驱散：90秒CD，受到的所有伤害降低50%，持续1.5秒，可以在被控制时使用。', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/11311.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (12211, '梦魇之牙', 2, 1056, 2050, '+240法术攻击 +5%移速', '唯一被动-重伤：造成伤害使得目标的生命恢复效果减少50%，持续1.5秒（如果该伤害由普攻触发，则持续时间延长至3秒）', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/12211.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (13310, '冰痕之握', 3, 1242, 2020, '+800最大生命 +500最大法力 +10%冷却缩减 +200物理防御', '唯一被动-强击：使用技能后，5秒内的下一次普攻造成范围30%减速（远程英雄使用时减速效果衰减为20%）与（150+英雄等级*20）点物理伤害，这个效果有3秒的冷却时间', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/13310.jpg')");
        db.execSQL( "INSERT INTO `equip` VALUES (91040, '逐日之弓', 1, 890, 2100, '攻击速度+25%\n 暴击+15%', '唯一被动-精准：普通攻击伤害提升35点，远程英雄使用时该效果翻倍。 \n唯一主动-逐日：增加自己150点普攻射程和40%移动速度，持续5秒，CD60秒（仅对远程英雄生效）', 'http://game.gtimg.cn/images/yxzj/img201606/itemimg/91040.jpg')");



        c.close();
        db.close();
    }



    public void initInscription(){
        String [] insertInscription ={
                "INSERT INTO `inscription` VALUES ('1501', '红色', '5', '圣人', '法术攻击力+5.3', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1501.png');",
                "INSERT INTO `inscription` VALUES ('1503', '红色', '5', '传承', '物理攻击力+3.2', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1503.png');",
                "INSERT INTO `inscription` VALUES ('1504', '红色', '5', '异变', '物理攻击力+2 物理穿透+3.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1504.png');",
                "INSERT INTO `inscription` VALUES ('1505', '红色', '5', '纷争', '物理攻击力+2.5 物理吸血+0.5%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1505.png');",
                "INSERT INTO `inscription` VALUES ('1510', '红色', '5', '无双', '暴击率+0.7% 暴击效果+3.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1510.png');",
                "INSERT INTO `inscription` VALUES ('1512', '红色', '5', '宿命', '攻速加成+1% 最大生命+33.7 物理防御力+2.3', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1512.png');",
                "INSERT INTO `inscription` VALUES ('1514', '红色', '5', '梦魇', '法术攻击力+4.2 法术穿透+2.4', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1514.png');",
                "INSERT INTO `inscription` VALUES ('1517', '红色', '5', '凶兆', '法术攻击力+4.2 攻速加成+0.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1517.png');",
                "INSERT INTO `inscription` VALUES ('1519', '红色', '5', '祸源', '暴击率+1.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1519.png');",
                "INSERT INTO `inscription` VALUES ('1520', '红色', '5', '红月', '攻速加成+1.6% 暴击率+0.5%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1520.png');",
                "INSERT INTO `inscription` VALUES ('2501', '蓝色', '5', '长生', '最大生命+75', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2501.png');",
                "INSERT INTO `inscription` VALUES ('2503', '蓝色', '5', '贪婪', '法术吸血+1.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2503.png');",
                "INSERT INTO `inscription` VALUES ('2504', '蓝色', '5', '夺萃', '物理吸血+1.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2504.png');",
                "INSERT INTO `inscription` VALUES ('2506', '蓝色', '5', '兽痕', '暴击率+0.5% 最大生命+60', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2506.png');",
                "INSERT INTO `inscription` VALUES ('2510', '蓝色', '5', '冥想', '最大生命+60 生命回复+4.5', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2510.png');",
                "INSERT INTO `inscription` VALUES ('2511', '蓝色', '5', '繁荣', '物理吸血+1% 法术防御力+4.1', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2511.png');",
                "INSERT INTO `inscription` VALUES ('2512', '蓝色', '5', '轮回', '法术攻击力+2.4 法术吸血+1%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2512.png');",
                "INSERT INTO `inscription` VALUES ('2515', '蓝色', '5', '调和', '最大生命+45 生命回复+5.2 移速+0.4%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2515.png');",
                "INSERT INTO `inscription` VALUES ('2517', '蓝色', '5', '隐匿', '物理攻击力+1.6 移速+1%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2517.png');",
                "INSERT INTO `inscription` VALUES ('2520', '蓝色', '5', '狩猎', '攻速加成+1% 移速+1%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2520.png');",
                "INSERT INTO `inscription` VALUES ('3501', '绿色', '5', '霸者', '物理防御力+9', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3501.png');",
                "INSERT INTO `inscription` VALUES ('3503', '绿色', '5', '均衡', '物理防御力+5 法术防御力+5', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3503.png');",
                "INSERT INTO `inscription` VALUES ('3509', '绿色', '5', '虚空', '最大生命+37.5 冷却缩减+0.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3509.png');",
                "INSERT INTO `inscription` VALUES ('3510', '绿色', '5', '灵山', '法术防御力+9', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3510.png');",
                "INSERT INTO `inscription` VALUES ('3511', '绿色', '5', '献祭', '法术攻击力+2.4 冷却缩减+0.7%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3511.png');",
                "INSERT INTO `inscription` VALUES ('3514', '绿色', '5', '鹰眼', '物理攻击力+0.9 物理穿透+6.4', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3514.png');",
                "INSERT INTO `inscription` VALUES ('3515', '绿色', '5', '心眼', '攻速加成+0.6% 法术穿透+6.4', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3515.png');",
                "INSERT INTO `inscription` VALUES ('3516', '绿色', '5', '怜悯', '冷却缩减+1%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3516.png');",
                "INSERT INTO `inscription` VALUES ('3517', '绿色', '5', '敬畏', '法术吸血+0.7% 物理防御力+5.9', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3517.png');",
                "INSERT INTO `inscription` VALUES ('3518', '绿色', '5', '回声', '物理防御力+2.7 法术防御力+2.7 冷却缩减+0.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3518.png');",
                "INSERT INTO `inscription` VALUES ('1403', '红色', '4', '衰败', '攻速加成+1.2%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1403.png');",
                "INSERT INTO `inscription` VALUES ('1405', '红色', '4', '暴戾', '物理攻击力+1.5 最大生命+13.5', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1405.png');",
                "INSERT INTO `inscription` VALUES ('1407', '红色', '4', '荆棘', '物理攻击力+1.5 攻速加成+0.4%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1407.png');",
                "INSERT INTO `inscription` VALUES ('1408', '红色', '4', '风暴', '攻速加成+0.6% 暴击率+0.3% 暴击效果+1.1%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1408.png');",
                "INSERT INTO `inscription` VALUES ('1410', '红色', '4', '戒律', '法术攻击力+2.5 暴击率+0.3%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1410.png');",
                "INSERT INTO `inscription` VALUES ('1412', '红色', '4', '阳炎', '法术攻击力+2.5 法术穿透+1.4', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1412.png');",
                "INSERT INTO `inscription` VALUES ('1414', '红色', '4', '惩戒', '暴击率+1%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1414.png');",
                "INSERT INTO `inscription` VALUES ('1415', '红色', '4', '狂热', '暴击率+0.5% 暴击效果+2%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1415.png');",
                "INSERT INTO `inscription` VALUES ('2401', '蓝色', '4', '气数', '最大生命+45', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2401.png');",
                "INSERT INTO `inscription` VALUES ('2402', '蓝色', '4', '刹那', '最大生命+13.5 移速+0.7%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2402.png');",
                "INSERT INTO `inscription` VALUES ('2403', '蓝色', '4', '复苏', '生命回复+9', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2403.png');",
                "INSERT INTO `inscription` VALUES ('2404', '蓝色', '4', '渴血', '法术攻击力+1.4 法术吸血+0.8% 法术防御力+1.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2404.png');",
                "INSERT INTO `inscription` VALUES ('2405', '蓝色', '4', '吞噬', '攻速加成+0.4% 物理吸血+0.8%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2405.png');",
                "INSERT INTO `inscription` VALUES ('2406', '蓝色', '4', '正义', '物理攻击力+0.6 最大生命+36', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2406.png');",
                "INSERT INTO `inscription` VALUES ('2409', '蓝色', '4', '滋生', '最大生命+36 物理防御力+1.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2409.png');",
                "INSERT INTO `inscription` VALUES ('2412', '蓝色', '4', '急救', '攻速加成+0.4% 暴击率+0.3% 移速+0.5%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2412.png');",
                "INSERT INTO `inscription` VALUES ('3401', '绿色', '4', '铁躯', '物理防御力+5.4', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3401.png');",
                "INSERT INTO `inscription` VALUES ('3402', '绿色', '4', '无畏', '法术防御力+5.4', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3402.png');",
                "INSERT INTO `inscription` VALUES ('3403', '绿色', '4', '奇袭', '攻速加成+0.4% 冷却缩减+0.5%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3403.png');",
                "INSERT INTO `inscription` VALUES ('3405', '绿色', '4', '庇护', '生命回复+4.5 物理防御力+3.2', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3405.png');",
                "INSERT INTO `inscription` VALUES ('3409', '绿色', '4', '憎恶', '法术吸血+0.5% 法术防御力+3.2', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3409.png');",
                "INSERT INTO `inscription` VALUES ('3411', '绿色', '4', '侵蚀', '法术攻击力+0.9 法术穿透+3.8', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3411.png');",
                "INSERT INTO `inscription` VALUES ('3412', '绿色', '4', '潜能', '最大生命+15.7 生命回复+3.1 冷却缩减+0.3%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3412.png');",
                "INSERT INTO `inscription` VALUES ('3414', '绿色', '4', '野性', '最大生命+13.5 物理穿透+3.8', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3414.png');",
                "INSERT INTO `inscription` VALUES ('1301', '红色', '3', '致命', '物理攻击力+1.3', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1301.png');",
                "INSERT INTO `inscription` VALUES ('1302', '红色', '3', '恐惧', '法术攻击力+2.1', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1302.png');",
                "INSERT INTO `inscription` VALUES ('1303', '红色', '3', '振奋', '攻速加成+0.5% 暴击率+0.3%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1303.png');",
                "INSERT INTO `inscription` VALUES ('1305', '红色', '3', '拯救', '物理攻击力+1 物理穿透+1', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1305.png');",
                "INSERT INTO `inscription` VALUES ('1307', '红色', '3', '一闪', '暴击率+0.3% 暴击效果+1.3%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1307.png');",
                "INSERT INTO `inscription` VALUES ('1308', '红色', '3', '信念', '法术攻击力+1.7 法术吸血+0.2%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1308.png');",
                "INSERT INTO `inscription` VALUES ('2303', '蓝色', '3', '饮血', '物理吸血+0.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2303.png');",
                "INSERT INTO `inscription` VALUES ('2304', '蓝色', '3', '转换', '法术吸血+0.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2304.png');",
                "INSERT INTO `inscription` VALUES ('2305', '蓝色', '3', '强健', '物理攻击力+0.4 最大生命+24', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2305.png');",
                "INSERT INTO `inscription` VALUES ('2307', '蓝色', '3', '感应', '法术攻击力+0.8 法术吸血+0.4%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2307.png');",
                "INSERT INTO `inscription` VALUES ('2308', '蓝色', '3', '绽放', '最大生命+12 生命回复+4.2', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2308.png');",
                "INSERT INTO `inscription` VALUES ('2310', '蓝色', '3', '神速', '物理吸血+0.4% 移速+0.3%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2310.png');",
                "INSERT INTO `inscription` VALUES ('3303', '绿色', '3', '贯穿', '物理穿透+3.2', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3303.png');",
                "INSERT INTO `inscription` VALUES ('3304', '绿色', '3', '破魔', '法术穿透+3.2', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3304.png');",
                "INSERT INTO `inscription` VALUES ('3306', '绿色', '3', '风怒', '物理攻击力+0.4 法术防御力+2.9', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3306.png');",
                "INSERT INTO `inscription` VALUES ('3307', '绿色', '3', '收割', '物理防御力+1.1 物理穿透+2.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3307.png');",
                "INSERT INTO `inscription` VALUES ('3308', '绿色', '3', '崩坏', '法术攻击力+0.6 法术穿透+2.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3308.png');",
                "INSERT INTO `inscription` VALUES ('3310', '绿色', '3', '突进', '生命回复+2.4 冷却缩减+0.3%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3310.png');",
                "INSERT INTO `inscription` VALUES ('1201', '红色', '2', '白刃', '物理攻击力+0.7 攻速加成+0.2%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1201.png');",
                "INSERT INTO `inscription` VALUES ('1203', '红色', '2', '震击', '暴击率+0.5%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1203.png');",
                "INSERT INTO `inscription` VALUES ('1204', '红色', '2', '痛苦', '法术攻击力+1.2 暴击率+0.1%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1204.png');",
                "INSERT INTO `inscription` VALUES ('1205', '红色', '2', '践踏', '攻速加成+0.6%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1205.png');",
                "INSERT INTO `inscription` VALUES ('2201', '蓝色', '2', '生长', '最大生命+21', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2201.png');",
                "INSERT INTO `inscription` VALUES ('2202', '蓝色', '2', '愈合', '生命回复+4.2', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2202.png');",
                "INSERT INTO `inscription` VALUES ('2203', '蓝色', '2', '刚毅', '物理攻击力+0.3 物理吸血+0.4%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2203.png');",
                "INSERT INTO `inscription` VALUES ('2204', '蓝色', '2', '吸收', '法术攻击力+0.4 法术吸血+0.4%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2204.png');",
                "INSERT INTO `inscription` VALUES ('3201', '绿色', '2', '坚壁', '物理防御力+2.5', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3201.png');",
                "INSERT INTO `inscription` VALUES ('3202', '绿色', '2', '幻盾', '法术防御力+2.5', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3202.png');",
                "INSERT INTO `inscription` VALUES ('3204', '绿色', '2', '破甲', '攻速加成+0.2% 物理穿透+1.8', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3204.png');",
                "INSERT INTO `inscription` VALUES ('3205', '绿色', '2', '洞察', '暴击率+0.1% 法术穿透+1.8', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3205.png');",
                "INSERT INTO `inscription` VALUES ('1101', '红色', '1', '勇气', '物理攻击力+0.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1101.png');",
                "INSERT INTO `inscription` VALUES ('1102', '红色', '1', '斗志', '法术攻击力+1.1', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1102.png');",
                "INSERT INTO `inscription` VALUES ('1103', '红色', '1', '猛攻', '攻速加成+0.4%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/1103.png');",
                "INSERT INTO `inscription` VALUES ('2101', '蓝色', '1', '活力', '最大生命+15', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2101.png');",
                "INSERT INTO `inscription` VALUES ('2102', '蓝色', '1', '治疗', '生命回复+3', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2102.png');",
                "INSERT INTO `inscription` VALUES ('2105', '蓝色', '1', '疾行', '移速+0.3%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/2105.png');",
                "INSERT INTO `inscription` VALUES ('3103', '绿色', '1', '穿刺', '物理穿透+1.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3103.png');",
                "INSERT INTO `inscription` VALUES ('3104', '绿色', '1', '专注', '法术穿透+1.6', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3104.png');",
                "INSERT INTO `inscription` VALUES ('3105', '绿色', '1', '应激', '冷却缩减+0.2%', 'http://game.gtimg.cn/images/yxzj/img201606/mingwen/3105.png');"};
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor1 = db.query(INSCRIPTION_TABLE,null,null,null,null,null,null);
        if(cursor1.getCount()==0)
        {
            for(int i =0;i<insertInscription.length;i++){
                db.execSQL(insertInscription[i]);
                cursor1.close();
            }
        }
        cursor1.close();
        db.close();
    }
}
