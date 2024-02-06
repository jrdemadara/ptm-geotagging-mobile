package com.jrdemadara.ptm_geotagging.server

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocalDatabase(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {

    companion object {
        private const val DATABASE_NAME = "geotagging"
        private const val DATABASE_VERSION = 1

        /* Tables */
        private const val TABLE_USERS = "users"
        private const val TABLE_PROFILES = "profiles"
        private const val TABLE_BENEFICIARIES = "beneficiaries"
        private const val TABLE_LIVELIHOOD = "livelihoods"
        private const val TABLE_SKILLS = "skills"

        /* User Table */
        private const val USER_ID_COL = "id"
        private const val USER_NAME_COL = "name"
        private const val USER_EMAIL_COL = "email"
        private const val USER_PASSWORD_COL = "password"

        /* Profiles Table */
        private const val PROFILE_ID_COL = "id"
        private const val PROFILE_LASTNAME_COL = "lastname"
        private const val PROFILE_FIRSTNAME_COL = "firstname"
        private const val PROFILE_MIDDLENAME_COL = "middlename"
        private const val PROFILE_EXTENSION_COL = "extension"
        private const val PROFILE_BIRTHDATE_COL = "birthdate"
        private const val PROFILE_OCCUPATION_COL = "occupation"
        private const val PROFILE_PHONE_COL = "phone"
        private const val PROFILE_LAT_COL = "lat"
        private const val PROFILE_LON_COL = "lon"
        private const val PROFILE_USER_ID_COL = "user_id"

        /* Beneficiaries Table */
        private const val BENEFICIARY_ID_COL = "id"
        private const val BENEFICIARY_PRECINT_COL = "precint"
        private const val BENEFICIARY_FULLNAME_COL = "fullname"
        private const val BENEFICIARY_BIRTHDATE_COL = "birthdate"
        private const val BENEFICIARY_PROFILE_ID_COL = "profile_id"

        /* Livelihoods Table */
        private const val LIVELIHOOD_ID_COL = "id"
        private const val LIVELIHOOD_LIVELIHOOD_COL = "livelihood"
        private const val LIVELIHOOD_PROFILE_ID_COL = "profile_id"

        /* Skills Table */
        private const val SKILL_ID_COL = "id"
        private const val SKILL_SKILL_COL = "skill"
        private const val SKILL_PROFILE_ID_COL = "profile_id"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = (
                "CREATE TABLE " +
                        TABLE_USERS + " (" +
                        USER_ID_COL + " TEXT, " +
                        USER_NAME_COL + " TEXT," +
                        USER_EMAIL_COL + " TEXT," +
                        USER_PASSWORD_COL + " TEXT)"
                )

        val createProfilesTable = (
                "CREATE TABLE " +
                        TABLE_PROFILES + " (" +
                        PROFILE_ID_COL + " TEXT, " +
                        PROFILE_LASTNAME_COL + " TEXT," +
                        PROFILE_FIRSTNAME_COL + " TEXT," +
                        PROFILE_MIDDLENAME_COL + " TEXT)" +
                        PROFILE_EXTENSION_COL + " TEXT)" +
                        PROFILE_BIRTHDATE_COL + " TEXT)" +
                        PROFILE_OCCUPATION_COL + " TEXT)" +
                        PROFILE_PHONE_COL + " TEXT)" +
                        PROFILE_LAT_COL + " TEXT)" +
                        PROFILE_LON_COL + " TEXT)" +
                        PROFILE_USER_ID_COL + " TEXT)"
                )

        val createBeneficiariesTable = (
                "CREATE TABLE " +
                        TABLE_BENEFICIARIES + " (" +
                        BENEFICIARY_ID_COL + " TEXT, " +
                        BENEFICIARY_PRECINT_COL + " TEXT," +
                        BENEFICIARY_FULLNAME_COL + " TEXT," +
                        BENEFICIARY_BIRTHDATE_COL + " TEXT)" +
                        BENEFICIARY_PROFILE_ID_COL + " TEXT,"
                )

        val createLivelihoodsTable = (
                "CREATE TABLE " +
                        TABLE_LIVELIHOOD + " (" +
                        LIVELIHOOD_ID_COL + " TEXT, " +
                        LIVELIHOOD_LIVELIHOOD_COL + " TEXT," +
                        LIVELIHOOD_PROFILE_ID_COL + " TEXT,"
                )

        val createSkillsTable = (
                "CREATE TABLE " +
                        TABLE_SKILLS + " (" +
                        SKILL_ID_COL + " TEXT, " +
                        SKILL_SKILL_COL + " TEXT," +
                        SKILL_PROFILE_ID_COL + " TEXT,"
                )

        db?.execSQL(createUserTable)
        db?.execSQL(createProfilesTable)
        db?.execSQL(createBeneficiariesTable)
        db?.execSQL(createLivelihoodsTable)
        db?.execSQL(createSkillsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BENEFICIARIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIVELIHOOD")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SKILLS")
        onCreate(db)
    }
    }