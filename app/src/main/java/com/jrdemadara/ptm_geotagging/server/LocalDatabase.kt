package com.jrdemadara.ptm_geotagging.server

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jrdemadara.ptm_geotagging.data.Assistance
import com.jrdemadara.ptm_geotagging.data.Beneficiary
import com.jrdemadara.ptm_geotagging.data.Livelihood
import com.jrdemadara.ptm_geotagging.data.Photo
import com.jrdemadara.ptm_geotagging.data.PowerSearchData
import com.jrdemadara.ptm_geotagging.data.Profile
import com.jrdemadara.ptm_geotagging.data.SearchMembers
import com.jrdemadara.ptm_geotagging.data.Tesda
import com.jrdemadara.ptm_geotagging.features.profile_details.assistance.DetailsAssistance
import com.jrdemadara.ptm_geotagging.features.profile_details.beneficiary.DetailsBeneficiaries
import com.jrdemadara.ptm_geotagging.features.profile_details.livelihood.DetailsLivelihood
import com.jrdemadara.ptm_geotagging.features.profile_details.skills.DetailsSkills
import com.jrdemadara.ptm_geotagging.features.profile_details.tesda.DetailsTesda
import com.jrdemadara.ptm_geotagging.features.profiling.skill.Skills

class LocalDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "geotagging"
        private const val DATABASE_VERSION = 1

        /* Tables */
        private const val TABLE_PROFILES = "profiles"
        private const val TABLE_BENEFICIARIES = "beneficiaries"
        private const val TABLE_LIVELIHOOD = "livelihoods"
        private const val TABLE_SKILLS = "skills"
        private const val TABLE_PHOTOS = "photos"
        private const val TABLE_MUNICIPALITIES = "municipalities"
        private const val TABLE_BARANGAYS = "barangays"
        private const val TABLE_MEMBERS = "members"
        private const val TABLE_ASSISTANCE = "assistance"
        private const val TABLE_ASSISTANCE_TYPE = "assistance_type"
        private const val TABLE_TESDA = "tesda"

        /* Profiles Table */
        private const val PROFILE_ID_COL = "id"
        private const val PROFILE_PRECINCT_COL = "precinct"
        private const val PROFILE_LASTNAME_COL = "lastname"
        private const val PROFILE_FIRSTNAME_COL = "firstname"
        private const val PROFILE_MIDDLENAME_COL = "middlename"
        private const val PROFILE_EXTENSION_COL = "extension"
        private const val PROFILE_BIRTHDATE_COL = "birthdate"
        private const val PROFILE_OCCUPATION_COL = "occupation"
        private const val PROFILE_PHONE_COL = "phone"
        private const val PROFILE_LAT_COL = "lat"
        private const val PROFILE_LON_COL = "lon"
        private const val PROFILE_BARANGAY_COL = "barangay"
        private const val PROFILE_PUROK_COL = "purok"
        private const val PROFILE_QR_COL = "qrcode"
        private const val PROFILE_HASPTMID_COL = "has_ptmid"
        private const val PROFILE_MUSLIM_COL = "is_muslim"
        private const val PROFILE_IS_UPLOADED_COL = "is_uploaded"

        /* Beneficiaries Table */
        private const val BENEFICIARY_ID_COL = "id"
        private const val BENEFICIARY_PRECINCT_COL = "precinct"
        private const val BENEFICIARY_FULLNAME_COL = "fullname"
        private const val BENEFICIARY_BIRTHDATE_COL = "birthdate"
        private const val BENEFICIARY_QR_COL = "qrcode"
        private const val BENEFICIARY_MUSLIM_COL = "is_muslim"
        private const val BENEFICIARY_PROFILE_ID_COL = "profile_id"


        /* Livelihoods Table */
        private const val LIVELIHOOD_ID_COL = "id"
        private const val LIVELIHOOD_LIVELIHOOD_COL = "livelihood"
        private const val LIVELIHOOD_LIVELIHOOD_DETAILS_COL = "details"
        private const val LIVELIHOOD_PROFILE_ID_COL = "profile_id"

        /* Skills Table */
        private const val SKILL_ID_COL = "id"
        private const val SKILL_SKILL_COL = "skill"
        private const val SKILL_PROFILE_ID_COL = "profile_id"

        /* Photos Table */
        private const val PHOTO_ID_COL = "id"
        private const val PHOTO_PERSONAL_COL = "photo_personal"
        private const val PHOTO_FAMILY_COL = "photo_family"
        private const val PHOTO_LIVELIHOOD_COL = "photo_livelihood"
        private const val PHOTO_PROFILE_ID_COL = "profile_id"

        /* Municipalities Table */
        private const val MUNICIPALITY_NAME_COL = "name"

        /* Barangays Table */
        private const val BARANGAY_NAME_COL = "name"

        /* Members Table */
        private const val MEMBER_ID_COL = "id"
        private const val MEMBER_PRECINCT_COL = "precinct"
        private const val MEMBER_LASTNAME_COL = "lastname"
        private const val MEMBER_FIRSTNAME_COL = "firstname"
        private const val MEMBER_MIDDLENAME_COL = "middlename"
        private const val MEMBER_EXTENSION_COL = "extension"
        private const val MEMBER_BIRTHDATE_COL = "birthdate"
        private const val MEMBER_CONTACT_COL = "contact"
        private const val MEMBER_OCCUPATION_COL = "occupation"
        private const val MEMBER_ISPTMID_COL = "has_ptmid"

        /* Assistance Table */
        private const val ASSISTANCE_ID = "id"
        private const val ASSISTANCE_ASSISTANCE_COL = "assistance"
        private const val ASSISTANCE_AMOUNT_COL = "amount"
        private const val ASSISTANCE_RELEASED_AT_COL = "released_at"
        private const val ASSISTANCE_PROFILE_ID_COL = "profile_id"

        /* Assistance Type Table */
        private const val ASSISTANCE_TYPE = "assistance_type"

        /* Tesda Table */
        private const val TESDA_ID_COL = "id"
        private const val TESDA_NAME_COL = "name"
        private const val TESDA_COURSE_COL = "course"
        private const val TESDA_PROFILE_ID_COL = "profile_id"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createProfilesTable = (
                "CREATE TABLE " +
                        TABLE_PROFILES + " (" +
                        PROFILE_ID_COL + " TEXT, " +
                        PROFILE_PRECINCT_COL + " TEXT," +
                        PROFILE_LASTNAME_COL + " TEXT," +
                        PROFILE_FIRSTNAME_COL + " TEXT," +
                        PROFILE_MIDDLENAME_COL + " TEXT," +
                        PROFILE_EXTENSION_COL + " TEXT," +
                        PROFILE_BIRTHDATE_COL + " TEXT," +
                        PROFILE_OCCUPATION_COL + " TEXT," +
                        PROFILE_PHONE_COL + " TEXT," +
                        PROFILE_LAT_COL + " TEXT," +
                        PROFILE_LON_COL + " TEXT," +
                        PROFILE_BARANGAY_COL + " TEXT," +
                        PROFILE_PUROK_COL + " TEXT," +
                        PROFILE_QR_COL + " TEXT," +
                        PROFILE_HASPTMID_COL + " INTEGER," +
                        PROFILE_MUSLIM_COL + " INTEGER," +
                        PROFILE_IS_UPLOADED_COL + " INTEGER)"
                )

        val createBeneficiariesTable = (
                "CREATE TABLE " +
                        TABLE_BENEFICIARIES + " (" +
                        BENEFICIARY_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BENEFICIARY_PRECINCT_COL + " TEXT," +
                        BENEFICIARY_FULLNAME_COL + " TEXT," +
                        BENEFICIARY_BIRTHDATE_COL + " TEXT," +
                        BENEFICIARY_QR_COL + " TEXT," +
                        BENEFICIARY_MUSLIM_COL + " INTEGER," +
                        BENEFICIARY_PROFILE_ID_COL + " TEXT)"
                )

        val createLivelihoodsTable = (
                "CREATE TABLE " +
                        TABLE_LIVELIHOOD + " (" +
                        LIVELIHOOD_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        LIVELIHOOD_LIVELIHOOD_COL + " TEXT," +
                        LIVELIHOOD_LIVELIHOOD_DETAILS_COL + " TEXT," +
                        LIVELIHOOD_PROFILE_ID_COL + " TEXT)"
                )

        val createSkillsTable = (
                "CREATE TABLE " +
                        TABLE_SKILLS + " (" +
                        SKILL_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SKILL_SKILL_COL + " TEXT," +
                        SKILL_PROFILE_ID_COL + " TEXT)"
                )

        val createPhotosTable = (
                "CREATE TABLE " +
                        TABLE_PHOTOS + " (" +
                        PHOTO_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PHOTO_PERSONAL_COL + " BLOB," +
                        PHOTO_FAMILY_COL + " BLOB," +
                        PHOTO_LIVELIHOOD_COL + " BLOB," +
                        PHOTO_PROFILE_ID_COL + " TEXT)"
                )

        val createMunicipalitiesTable = (
                "CREATE TABLE " +
                        TABLE_MUNICIPALITIES + " (" +
                        MUNICIPALITY_NAME_COL + " TEXT)"
                )

        val createBarangaysTable = (
                "CREATE TABLE " +
                        TABLE_BARANGAYS + " (" +
                        BARANGAY_NAME_COL + " TEXT)"
                )

        val createMembersTable = (
                "CREATE TABLE " +
                        TABLE_MEMBERS + " (" +
                        MEMBER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MEMBER_PRECINCT_COL + " TEXT," +
                        MEMBER_LASTNAME_COL + " TEXT," +
                        MEMBER_FIRSTNAME_COL + " TEXT," +
                        MEMBER_MIDDLENAME_COL + " TEXT," +
                        MEMBER_EXTENSION_COL + " TEXT," +
                        MEMBER_BIRTHDATE_COL + " TEXT," +
                        MEMBER_CONTACT_COL + " TEXT," +
                        MEMBER_OCCUPATION_COL + " TEXT," +
                        MEMBER_ISPTMID_COL + " INTEGER)"

                )

        val createAssistanceTable = (
                "CREATE TABLE " +
                        TABLE_ASSISTANCE + " (" +
                        ASSISTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ASSISTANCE_ASSISTANCE_COL + " TEXT," +
                        ASSISTANCE_AMOUNT_COL + " TEXT," +
                        ASSISTANCE_RELEASED_AT_COL + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        ASSISTANCE_PROFILE_ID_COL + " TEXT)"
                )

        val createAssistanceTypeTable = (
                "CREATE TABLE " +
                        TABLE_ASSISTANCE_TYPE + " (" +
                        ASSISTANCE_TYPE + " TEXT)"
                )

        val createTesdaTable = (
                "CREATE TABLE " +
                        TABLE_TESDA + " (" +
                        TESDA_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TESDA_NAME_COL + " TEXT," +
                        TESDA_COURSE_COL + " TEXT," +
                        TESDA_PROFILE_ID_COL + " TEXT)"
                )

        db?.execSQL(createProfilesTable)
        db?.execSQL(createBeneficiariesTable)
        db?.execSQL(createLivelihoodsTable)
        db?.execSQL(createSkillsTable)
        db?.execSQL(createPhotosTable)
        db?.execSQL(createMunicipalitiesTable)
        db?.execSQL(createMembersTable)
        db?.execSQL(createAssistanceTable)
        db?.execSQL(createAssistanceTypeTable)
        db?.execSQL(createTesdaTable)
        db?.execSQL(createBarangaysTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BENEFICIARIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIVELIHOOD")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SKILLS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PHOTOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MUNICIPALITIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BARANGAYS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MEMBERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ASSISTANCE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ASSISTANCE_TYPE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TESDA")
        onCreate(db)
    }

    fun truncateTables() {
        val db = this.writableDatabase
        db.delete(TABLE_MUNICIPALITIES, null, null)
        db.close()
    }

    fun truncateMembers() {
        val db = this.writableDatabase
        db.delete(TABLE_MEMBERS, null, null)
        db.close()
    }

    fun truncateAssistanceType() {
        val db = this.writableDatabase
        db.delete(TABLE_ASSISTANCE_TYPE, null, null)
        db.close()
    }

    /* Municipalities */
    fun updateMunicipalities(name: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(MUNICIPALITY_NAME_COL, name)
        db.insert(TABLE_MUNICIPALITIES, null, values)
        db.close()
    }

    /* Barangays */
    fun updateBarangays(name: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(BARANGAY_NAME_COL, name)
        db.insert(TABLE_BARANGAYS, null, values)
        db.close()
    }

    /* Members */
    fun updateMembers(
        precinct: String?,
        lastName: String?,
        firstName: String?,
        middleName: String?,
        extension: String?,
        birthdate: String?,
        contact: String?,
        occupation: String?,
        isPTMID: Int,
    ) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(MEMBER_PRECINCT_COL, precinct)
        values.put(MEMBER_LASTNAME_COL, lastName)
        values.put(MEMBER_FIRSTNAME_COL, firstName)
        values.put(MEMBER_MIDDLENAME_COL, middleName)
        values.put(MEMBER_EXTENSION_COL, extension)
        values.put(MEMBER_BIRTHDATE_COL, birthdate)
        values.put(MEMBER_CONTACT_COL, contact)
        values.put(MEMBER_OCCUPATION_COL, occupation)
        values.put(MEMBER_ISPTMID_COL, isPTMID)
        db.insert(TABLE_MEMBERS, null, values)
        db.close()
    }

    fun updateAssistanceType(
        assistance: String?,
    ) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ASSISTANCE_TYPE, assistance)
        db.insert(TABLE_ASSISTANCE_TYPE, null, values)
        db.close()
    }

    fun getMunicipalities(): ArrayList<String> {
        val modules: ArrayList<String> = ArrayList()
        val selectQuery = "SELECT name FROM $TABLE_MUNICIPALITIES ORDER BY name ASC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                modules.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return modules
    }

    fun getBarangays(): ArrayList<String> {
        val barangays: ArrayList<String> = ArrayList()
        val selectQuery = "SELECT name FROM $TABLE_BARANGAYS ORDER BY name ASC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                barangays.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return barangays
    }

    /* Save Attendance */
    fun saveProfile(
        id: String?,
        precinct: String?,
        lastname: String?,
        firstname: String?,
        middlename: String?,
        extension: String?,
        birthdate: String?,
        occupation: String?,
        phone: String?,
        latitude: String?,
        longitude: String?,
        barangay: String?,
        purok: String?,
        qrcode: String?,
        hasPTMID: Int?,
        isMuslim: Int?,
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if the profile with the same last name and first name already exists in the database
            val cursor = db.query(
                TABLE_PROFILES,
                arrayOf(PROFILE_ID_COL),
                "$PROFILE_PRECINCT_COL = ? AND $PROFILE_LASTNAME_COL = ? AND $PROFILE_FIRSTNAME_COL = ?",
                arrayOf(precinct, lastname, firstname),
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                // Profile already exists, don't save the data
                cursor.close()
                db.close()
                return false
            }

            cursor?.close()

            // Profile doesn't exist, proceed with saving the data
            val values = ContentValues()
            values.put(PROFILE_ID_COL, id)
            values.put(PROFILE_PRECINCT_COL, precinct)
            values.put(PROFILE_LASTNAME_COL, lastname)
            values.put(PROFILE_FIRSTNAME_COL, firstname)
            values.put(PROFILE_MIDDLENAME_COL, middlename)
            values.put(PROFILE_EXTENSION_COL, extension)
            values.put(PROFILE_BIRTHDATE_COL, birthdate)
            values.put(PROFILE_OCCUPATION_COL, occupation)
            values.put(PROFILE_PHONE_COL, phone)
            values.put(PROFILE_LAT_COL, latitude)
            values.put(PROFILE_LON_COL, longitude)
            values.put(PROFILE_BARANGAY_COL, barangay)
            values.put(PROFILE_PUROK_COL, purok)
            values.put(PROFILE_QR_COL, qrcode)
            values.put(PROFILE_IS_UPLOADED_COL, 0)
            values.put(PROFILE_HASPTMID_COL, hasPTMID)
            values.put(PROFILE_MUSLIM_COL, isMuslim)
            db.insert(TABLE_PROFILES, null, values)
            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun saveBeneficiaries(
        id: String?,
        precinct: String?,
        fullname: String?,
        birthdate: String?,
        qrcode: String?,
        isMuslim: Int?,
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if the precinct already exists in the database
            val cursor = db.query(
                TABLE_BENEFICIARIES,
                arrayOf(BENEFICIARY_FULLNAME_COL),
                "$BENEFICIARY_FULLNAME_COL = ?",
                arrayOf(fullname),
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                // Precinct already exists, don't save the data
                cursor.close()
                db.close()
                return false
            }

            cursor?.close()

            // Precinct doesn't exist, proceed with saving the data
            val values = ContentValues()
            values.put(BENEFICIARY_PRECINCT_COL, precinct)
            values.put(BENEFICIARY_FULLNAME_COL, fullname)
            values.put(BENEFICIARY_BIRTHDATE_COL, birthdate)
            values.put(BENEFICIARY_QR_COL, qrcode)
            values.put(BENEFICIARY_MUSLIM_COL, isMuslim)
            values.put(BENEFICIARY_PROFILE_ID_COL, id)
            db.insert(TABLE_BENEFICIARIES, null, values)

            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun saveSkills(
        id: String?,
        skill: String?
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if the skill already exists for the given profile ID
            val cursor = db.query(
                TABLE_SKILLS,
                arrayOf(SKILL_SKILL_COL),
                "$SKILL_SKILL_COL = ? AND $SKILL_PROFILE_ID_COL = ?",
                arrayOf(skill, id),
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                // Skill already exists for the given profile ID, don't save the data
                cursor.close()
                db.close()
                return false
            }

            cursor?.close()

            // Skill doesn't exist for the given profile ID, proceed with saving the data
            val values = ContentValues()
            values.put(SKILL_SKILL_COL, skill)
            values.put(SKILL_PROFILE_ID_COL, id)
            db.insert(TABLE_SKILLS, null, values)

            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun saveLivelihood(
        id: String?,
        livelihood: String?,
        details: String?
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if the livelihood already exists for the given profile ID
            val cursor = db.query(
                TABLE_LIVELIHOOD,
                arrayOf(LIVELIHOOD_LIVELIHOOD_COL),
                "$LIVELIHOOD_LIVELIHOOD_COL = ? AND $LIVELIHOOD_PROFILE_ID_COL = ?",
                arrayOf(livelihood, id),
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                // Livelihood already exists for the given profile ID, don't save the data
                cursor.close()
                db.close()
                return false
            }

            cursor?.close()

            // Livelihood doesn't exist for the given profile ID, proceed with saving the data
            val values = ContentValues()
            values.put(LIVELIHOOD_LIVELIHOOD_COL, livelihood)
            values.put(LIVELIHOOD_LIVELIHOOD_DETAILS_COL, details)
            values.put(LIVELIHOOD_PROFILE_ID_COL, id)
            db.insert(TABLE_LIVELIHOOD, null, values)

            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun saveTesda(
        id: String?,
        name: String?,
        course: String?
    ): Boolean {
        return try {
            val db = this.writableDatabase
            // Skill doesn't exist for the given profile ID, proceed with saving the data
            val values = ContentValues()
            values.put(TESDA_NAME_COL, name)
            values.put(TESDA_COURSE_COL, course)
            values.put(TESDA_PROFILE_ID_COL, id)
            db.insert(TABLE_TESDA, null, values)

            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun saveAssistance(
        id: String?,
        assistance: String?,
    ): Boolean {
        return try {
            val db = this.writableDatabase
            // Assistance doesn't exist for the given profile ID, proceed with saving the data
            val values = ContentValues()
            values.put(ASSISTANCE_ASSISTANCE_COL, assistance)
            values.put(ASSISTANCE_AMOUNT_COL, "0")
            values.put(ASSISTANCE_PROFILE_ID_COL, id)
            db.insert(TABLE_ASSISTANCE, null, values)

            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun savePhotos(
        id: String?,
        photoPersonal: ByteArray?,
        photoFamily: ByteArray?,
        photoLivelihood: ByteArray?
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if photos already exist for the given profile ID
            val cursor = db.query(
                TABLE_PHOTOS,
                arrayOf(PHOTO_PERSONAL_COL, PHOTO_FAMILY_COL, PHOTO_LIVELIHOOD_COL),
                "$PHOTO_PROFILE_ID_COL = ?",
                arrayOf(id),
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                // Photos already exist for the given profile ID, don't save the data
                cursor.close()
                db.close()
                return false
            }

            cursor?.close()

            // Photos don't exist for the given profile ID, proceed with saving the data
            val values = ContentValues()
            values.put(PHOTO_PERSONAL_COL, photoPersonal)
            values.put(PHOTO_FAMILY_COL, photoFamily)
            values.put(PHOTO_LIVELIHOOD_COL, photoLivelihood)
            values.put(PHOTO_PROFILE_ID_COL, id)
            db.insert(TABLE_PHOTOS, null, values)

            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun saveAssistance(
        id: String?,
        assistance: String?,
        amount: String?,
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if the assistance already exists for the given profile ID
            val cursor = db.query(
                TABLE_ASSISTANCE,
                arrayOf(ASSISTANCE_ID),
                "$ASSISTANCE_PROFILE_ID_COL = ? AND $ASSISTANCE_ASSISTANCE_COL = ?",
                arrayOf(id, assistance),
                null,
                null,
                null
            )

            if (cursor != null && cursor.count > 0) {
                // Assistance already exists for the given profile ID, don't save the data
                cursor.close()
                db.close()
                return false
            }

            cursor?.close()

            // Assistance doesn't exist for the given profile ID, proceed with saving the data
            val values = ContentValues()
            values.put(ASSISTANCE_ASSISTANCE_COL, assistance)
            values.put(ASSISTANCE_AMOUNT_COL, amount)
            values.put(ASSISTANCE_PROFILE_ID_COL, id)
            db.insert(TABLE_ASSISTANCE, null, values)

            db.close()
            true // Data saved successfully
        } catch (e: Exception) {
            // Handle any exceptions here
            e.printStackTrace()
            false // Data not saved successfully
        }
    }

    fun getProfileCount(): Int {
        var count = 0
        val selectQuery = "SELECT COUNT(*) FROM $TABLE_PROFILES"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        }
        db.close()
        return count
    }


    fun getUploadedCount(): Int {
        var count = 0
        val selectQuery = "SELECT COUNT(*) FROM $TABLE_PROFILES WHERE $PROFILE_IS_UPLOADED_COL = 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        }
        db.close()
        return count
    }

    fun getNotUploadedCount(): Int {
        var count = 0
        val selectQuery = "SELECT COUNT(*) FROM $TABLE_PROFILES WHERE $PROFILE_IS_UPLOADED_COL = 0"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        }
        db.close()
        return count
    }

    fun getSingleProfiles(profileID: String): ArrayList<Profile> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_PROFILES  WHERE $PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Profile> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Profile(
                        id = cursor.getString(0),
                        precinct = cursor.getString(1),
                        lastname = cursor.getString(2),
                        firstname = cursor.getString(3),
                        middlename = cursor.getString(4),
                        extension = cursor.getString(5),
                        birthdate = cursor.getString(6),
                        occupation = cursor.getString(7),
                        phone = cursor.getString(8),
                        lat = cursor.getString(9),
                        lon = cursor.getString(10),
                        barangay = cursor.getString(11),
                        purok = cursor.getString(12),
                        qrcode = cursor.getString(13),
                        hasptmid = cursor.getInt(14),
                        ismuslim = cursor.getInt(15),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    /* Get profiles to upload */
    fun getProfiles(): ArrayList<Profile> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_PROFILES WHERE $PROFILE_IS_UPLOADED_COL = 0"
        val data: ArrayList<Profile> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Profile(
                        id = cursor.getString(0),
                        precinct = cursor.getString(1),
                        lastname = cursor.getString(2),
                        firstname = cursor.getString(3),
                        middlename = cursor.getString(4),
                        extension = cursor.getString(5),
                        birthdate = cursor.getString(6),
                        occupation = cursor.getString(7),
                        phone = cursor.getString(8),
                        lat = cursor.getString(9),
                        lon = cursor.getString(10),
                        barangay = cursor.getString(11),
                        purok = cursor.getString(12),
                        qrcode = cursor.getString(13),
                        hasptmid = cursor.getInt(14),
                        ismuslim = cursor.getInt(15),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }



    fun getAllProfiles(): ArrayList<Profile> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_PROFILES"
        val data: ArrayList<Profile> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Profile(
                        id = cursor.getString(0),
                        precinct = cursor.getString(1),
                        lastname = cursor.getString(2),
                        firstname = cursor.getString(3),
                        middlename = cursor.getString(4),
                        extension = cursor.getString(5),
                        birthdate = cursor.getString(6),
                        occupation = cursor.getString(7),
                        phone = cursor.getString(8),
                        lat = cursor.getString(9),
                        lon = cursor.getString(10),
                        barangay = cursor.getString(11),
                        purok = cursor.getString(12),
                        qrcode = cursor.getString(13),
                        hasptmid = cursor.getInt(14),
                        ismuslim = cursor.getInt(15),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getBeneficiaries(profileID: String): ArrayList<Beneficiary> {
        val db = this.readableDatabase
        val query =
            "SELECT *  FROM $TABLE_BENEFICIARIES WHERE $BENEFICIARY_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Beneficiary> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Beneficiary(
                        precinct = cursor.getString(1),
                        fullname = cursor.getString(2),
                        birthdate = cursor.getString(3),
                        qrcode = cursor.getString(4),
                        ismuslim = cursor.getInt(5),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getProfileBeneficiaries(profileID: String): ArrayList<DetailsBeneficiaries> {
        val db = this.readableDatabase
        val query =
            "SELECT *  FROM $TABLE_BENEFICIARIES WHERE $BENEFICIARY_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<DetailsBeneficiaries> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    DetailsBeneficiaries(
                        precinct = cursor.getString(1),
                        fullname = cursor.getString(2),
                        birthdate = cursor.getString(3),
                        qrcode = cursor.getString(4),
                        ismuslim = cursor.getInt(5),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getSkills(profileID: String): ArrayList<Skills> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_SKILLS WHERE $SKILL_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Skills> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Skills(
                        skill = cursor.getString(1),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }
    fun getProfileSkills(profileID: String): ArrayList<DetailsSkills> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_SKILLS WHERE $SKILL_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<DetailsSkills> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    DetailsSkills(
                        skills = cursor.getString(1),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }


    fun getLivelihood(profileID: String): ArrayList<Livelihood> {
        val db = this.readableDatabase
        val query =
            "SELECT *  FROM $TABLE_LIVELIHOOD WHERE $LIVELIHOOD_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Livelihood> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Livelihood(
                        livelihood = cursor.getString(1),
                        description = cursor.getString(2),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getProfileLivelihood(profileID: String): ArrayList<DetailsLivelihood> {
        val db = this.readableDatabase
        val query =
            "SELECT *  FROM $TABLE_LIVELIHOOD WHERE $LIVELIHOOD_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<DetailsLivelihood> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    DetailsLivelihood(
                        livelihood = cursor.getString(1),
                        description = cursor.getString(2),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getTesdaAdminUpload(profileID: String): ArrayList<Tesda> {
        val db = this.readableDatabase
        val query =
            "SELECT *  FROM $TABLE_TESDA WHERE $TESDA_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Tesda> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Tesda(
                        name = cursor.getString(1),
                        course = cursor.getString(2),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getTesda(profileID: String): ArrayList<DetailsTesda> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_TESDA WHERE $TESDA_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<DetailsTesda> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    DetailsTesda(
                        name = cursor.getString(1),
                        course = cursor.getString(2),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getTesdaUploadIndividual(profileID: String): ArrayList<Tesda> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_TESDA WHERE $TESDA_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Tesda> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Tesda(
                        name = cursor.getString(1),
                        course = cursor.getString(2),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getPhotos(profileID: String): ArrayList<Photo> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_PHOTOS WHERE $PHOTO_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Photo> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Photo(
                        personal = cursor.getBlob(1),
                        family = cursor.getBlob(2),
                        livelihood = cursor.getBlob(3),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getAssistance(profileID: String): ArrayList<Assistance> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_ASSISTANCE WHERE $ASSISTANCE_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<Assistance> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Assistance(
                        assistance = cursor.getString(1),
                        amount = cursor.getString(2),
                        releasedAt = cursor.getString(3),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getAssistance(profileID: String, assistance: String): ArrayList<Assistance> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_ASSISTANCE WHERE $ASSISTANCE_PROFILE_ID_COL = '$profileID' AND $ASSISTANCE_ASSISTANCE_COL = '$assistance'"
        val data: ArrayList<Assistance> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Assistance(
                        assistance = cursor.getString(1),
                        amount = cursor.getString(2),
                        releasedAt = cursor.getString(3),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getDetailsAssistance(profileID: String): ArrayList<DetailsAssistance> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_ASSISTANCE" +
                " WHERE $ASSISTANCE_PROFILE_ID_COL = '$profileID'"
        val data: ArrayList<DetailsAssistance> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    DetailsAssistance(
                        assistance = cursor.getString(1),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getAssistanceType(): ArrayList<String> {
        val data: ArrayList<String> = ArrayList()
        val selectQuery =
            "SELECT $ASSISTANCE_TYPE FROM $TABLE_ASSISTANCE_TYPE ORDER BY $ASSISTANCE_TYPE ASC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                data.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return data
    }

    fun getProfileID(qrcode: String): String {
        var id = ""
        val selectQuery =
            "SELECT $PROFILE_ID_COL FROM $TABLE_PROFILES WHERE $PROFILE_QR_COL = '${qrcode}'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = cursor.getString(0)
            }
            cursor.close()
        }
        db.close()
        return id
    }

    fun getProfileName(qrcode: String): String {
        var name = ""
        val selectQuery =
            "SELECT lastname || ', ' || firstname || ' ' || middlename || ' ' || extension AS fullname  FROM $TABLE_PROFILES WHERE $PROFILE_QR_COL = '${qrcode}'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(0)
            }
            cursor.close()
        }
        db.close()
        return name
    }

    /* Search Worker */
    fun searchMember(name: String?): ArrayList<SearchMembers> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_MEMBERS\n" +
                "WHERE $MEMBER_LASTNAME_COL || ' ' || $MEMBER_FIRSTNAME_COL || ' ' || $MEMBER_MIDDLENAME_COL LIKE '%$name%' "
        val data: ArrayList<SearchMembers> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    SearchMembers(
                        precinct = cursor.getString(1),
                        lastname = cursor.getString(2),
                        firstname = cursor.getString(3),
                        middlename = cursor.getString(4),
                        extension = cursor.getString(5),
                        birthdate = cursor.getString(6),
                        contact = cursor.getString(7),
                        occupation = cursor.getString(8),
                        isptmid = cursor.getInt(9),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getProfilesPowerSearch(): ArrayList<PowerSearchData> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PROFILES"
        val data: ArrayList<PowerSearchData> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    PowerSearchData(
                        id = cursor.getString(0),
                        precinct = cursor.getString(1),
                        lastname = cursor.getString(2),
                        firstname = cursor.getString(3),
                        middlename = cursor.getString(4),
                        extension = cursor.getString(5),
                        birthdate = cursor.getString(6),
                        occupation = cursor.getString(7),
                        phone = cursor.getString(8),
                        qrcode = cursor.getString(11),
                        hasptmid = cursor.getInt(12),
                        isUploaded = cursor.getInt(13),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun powerSearch(name: String?): ArrayList<PowerSearchData> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PROFILES\n" +
                "WHERE $PROFILE_LASTNAME_COL || ' ' || $PROFILE_FIRSTNAME_COL || ' ' || $PROFILE_MIDDLENAME_COL LIKE '%$name%' "
        val data: ArrayList<PowerSearchData> = ArrayList()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    PowerSearchData(
                        id = cursor.getString(0),
                        precinct = cursor.getString(1),
                        lastname = cursor.getString(2),
                        firstname = cursor.getString(3),
                        middlename = cursor.getString(4),
                        extension = cursor.getString(5),
                        birthdate = cursor.getString(6),
                        occupation = cursor.getString(7),
                        phone = cursor.getString(8),
                        qrcode = cursor.getString(11),
                        hasptmid = cursor.getInt(12),
                        isUploaded = cursor.getInt(13),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun markUploaded(profileID: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(PROFILE_IS_UPLOADED_COL, 1)
        db.update(TABLE_PROFILES, values, "$PROFILE_ID_COL = ?", arrayOf(profileID))
        db.close()
    }
}