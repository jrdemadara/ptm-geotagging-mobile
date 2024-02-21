package com.jrdemadara.ptm_geotagging.server

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jrdemadara.ptm_geotagging.data.Beneficiary
import com.jrdemadara.ptm_geotagging.data.Livelihood
import com.jrdemadara.ptm_geotagging.data.Photo
import com.jrdemadara.ptm_geotagging.data.Profile
import com.jrdemadara.ptm_geotagging.data.ProfileWithDetails
import com.jrdemadara.ptm_geotagging.features.profiling.skill.Skills

class LocalDatabase(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {

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
            private const val PROFILE_IS_UPLOADED_COL = "is_uploaded"

            /* Beneficiaries Table */
            private const val BENEFICIARY_ID_COL = "id"
            private const val BENEFICIARY_PRECINCT_COL = "precinct"
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

            /* Photos Table */
            private const val PHOTO_ID_COL = "id"
            private const val PHOTO_PERSONAL_COL = "photo_personal"
            private const val PHOTO_FAMILY_COL = "photo_family"
            private const val PHOTO_LIVELIHOOD_COL = "photo_livelihood"
            private const val PHOTO_PROFILE_ID_COL = "profile_id"

            /* Municipalities Table */
            private const val MUNICIPALITY_NAME_COL = "name"

        }

        override fun onCreate(db: SQLiteDatabase?) {
            val createProfilesTable = (
                    "CREATE TABLE " +
                            TABLE_PROFILES + " (" +
                            PROFILE_ID_COL + " TEXT, " +
                            PROFILE_LASTNAME_COL + " TEXT," +
                            PROFILE_FIRSTNAME_COL + " TEXT," +
                            PROFILE_MIDDLENAME_COL + " TEXT," +
                            PROFILE_EXTENSION_COL + " TEXT," +
                            PROFILE_BIRTHDATE_COL + " TEXT," +
                            PROFILE_OCCUPATION_COL + " TEXT," +
                            PROFILE_PHONE_COL + " TEXT," +
                            PROFILE_LAT_COL + " TEXT," +
                            PROFILE_LON_COL + " TEXT," +
                            PROFILE_IS_UPLOADED_COL + " INTEGER)"
                    )

            val createBeneficiariesTable = (
                    "CREATE TABLE " +
                            TABLE_BENEFICIARIES + " (" +
                            BENEFICIARY_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            BENEFICIARY_PRECINCT_COL + " TEXT," +
                            BENEFICIARY_FULLNAME_COL + " TEXT," +
                            BENEFICIARY_BIRTHDATE_COL + " TEXT," +
                            BENEFICIARY_PROFILE_ID_COL + " TEXT)"
                    )

            val createLivelihoodsTable = (
                    "CREATE TABLE " +
                            TABLE_LIVELIHOOD + " (" +
                            LIVELIHOOD_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            LIVELIHOOD_LIVELIHOOD_COL + " TEXT," +
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

            db?.execSQL(createProfilesTable)
            db?.execSQL(createBeneficiariesTable)
            db?.execSQL(createLivelihoodsTable)
            db?.execSQL(createSkillsTable)
            db?.execSQL(createPhotosTable)
            db?.execSQL(createMunicipalitiesTable)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_BENEFICIARIES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIVELIHOOD")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_SKILLS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_PHOTOS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_MUNICIPALITIES")
            onCreate(db)
        }

        fun truncateTables() {
            val db = this.writableDatabase
            db.delete(TABLE_MUNICIPALITIES, null, null)
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

    /* Save Attendance */
    fun saveProfile(
        id: String?,
        lastname: String?,
        firstname: String?,
        middlename: String?,
        extension: String?,
        birthdate: String?,
        occupation: String?,
        phone: String?,
        latitude: String?,
        longitude: String?,
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if the profile with the same last name and first name already exists in the database
            val cursor = db.query(
                TABLE_PROFILES,
                arrayOf(PROFILE_ID_COL),
                "$PROFILE_LASTNAME_COL = ? AND $PROFILE_FIRSTNAME_COL = ?",
                arrayOf(lastname, firstname),
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
            values.put(PROFILE_LASTNAME_COL, lastname)
            values.put(PROFILE_FIRSTNAME_COL, firstname)
            values.put(PROFILE_MIDDLENAME_COL, middlename)
            values.put(PROFILE_EXTENSION_COL, extension)
            values.put(PROFILE_BIRTHDATE_COL, birthdate)
            values.put(PROFILE_OCCUPATION_COL, occupation)
            values.put(PROFILE_PHONE_COL, phone)
            values.put(PROFILE_LAT_COL, latitude)
            values.put(PROFILE_LON_COL, longitude)
            values.put(PROFILE_IS_UPLOADED_COL, 0)
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
        birthdate: String?
    ): Boolean {
        return try {
            val db = this.writableDatabase

            // Check if the precinct already exists in the database
            val cursor = db.query(
                TABLE_BENEFICIARIES,
                arrayOf(BENEFICIARY_PRECINCT_COL),
                "$BENEFICIARY_PRECINCT_COL = ?",
                arrayOf(precinct),
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
        livelihood: String?
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
                        lastname = cursor.getString(1),
                        firstname = cursor.getString(2),
                        middlename = cursor.getString(3),
                        extension = cursor.getString(4),
                        birthdate = cursor.getString(5),
                        occupation = cursor.getString(6),
                        phone = cursor.getString(7),
                        lat = cursor.getString(8),
                        lon = cursor.getString(9),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getBeneficiaries(profileID: String): ArrayList<Beneficiary> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_BENEFICIARIES WHERE $BENEFICIARY_PROFILE_ID_COL = '$profileID'"
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

    fun getLivelihood(profileID: String): ArrayList<Livelihood> {
        val db = this.readableDatabase
        val query = "SELECT *  FROM $TABLE_LIVELIHOOD WHERE $LIVELIHOOD_PROFILE_ID_COL = '$profileID'"
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

    fun markUploaded(profileID: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(PROFILE_IS_UPLOADED_COL, 1)
        db.update(TABLE_PROFILES, values, "$PROFILE_ID_COL = ?", arrayOf(profileID))
        db.close()
    }

//    fun getProfilesWithDetails(): List<ProfileWithDetails> {
//        val selectQuery = """
//        SELECT * FROM $TABLE_PROFILES
//        LEFT JOIN $TABLE_BENEFICIARIES ON $TABLE_PROFILES.$PROFILE_ID_COL = $TABLE_BENEFICIARIES.$BENEFICIARY_PROFILE_ID_COL
//        LEFT JOIN $TABLE_SKILLS ON $TABLE_PROFILES.$PROFILE_ID_COL = $TABLE_SKILLS.$SKILL_PROFILE_ID_COL
//        LEFT JOIN $TABLE_LIVELIHOOD ON $TABLE_PROFILES.$PROFILE_ID_COL = $TABLE_LIVELIHOOD.$LIVELIHOOD_PROFILE_ID_COL
//        LEFT JOIN $TABLE_PHOTOS ON $TABLE_PROFILES.$PROFILE_ID_COL = $TABLE_PHOTOS.$PHOTO_PROFILE_ID_COL
//        WHERE $TABLE_PROFILES.$PROFILE_IS_UPLOADED_COL = 0
//    """
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(selectQuery, null)
//
//        val profilesWithDetails = mutableListOf<ProfileWithDetails>()
//
//        try {
//            cursor.use {
//                while (cursor.moveToNext()) {
//                    val profile = Profile(
//                        id = cursor.getString(0),
//                        lastname = cursor.getString(1),
//                        firstname = cursor.getString(2),
//                        middlename = cursor.getString(3),
//                        extension = cursor.getString(4),
//                        birthdate = cursor.getString(5),
//                        occupation = cursor.getString(6),
//                        phone = cursor.getString(7),
//                        lat = cursor.getString(8),
//                        lon = cursor.getString(9),
//                    )
//
//                    val beneficiaries = mutableListOf<Beneficiary>()
//                    val skills = mutableListOf<String>()
//                    val livelihoods = mutableListOf<String>()
//                    val photo = Photo(
//                        personalPhoto = cursor.getBlob(23), // Assuming personal photo is at index 10
//                        familyPhoto = cursor.getBlob(24),   // Assuming family photo is at index 11
//                        livelihoodPhoto = cursor.getBlob(25) // Assuming livelihood photo is at index 12
//                    )
//
//                    // Extract beneficiary data
//                    val precinct = cursor.getString(12)
//                    val fullname = cursor.getString(13)
//                    val birthdate = cursor.getString(14)
//                    if (precinct != null && fullname != null && birthdate != null) {
//                        beneficiaries.add(Beneficiary(precinct, fullname, birthdate))
//                    }
//
//                    // Extract skill data
//                    val skill = cursor.getString(17)
//                    if (skill != null) {
//                        skills.add(skill)
//                    }
//
//                    // Extract livelihood data
//                    val livelihood = cursor.getString(20)
//                    if (livelihood != null) {
//                        livelihoods.add(livelihood)
//                    }
//
//                    val profileWithDetails = ProfileWithDetails(profile, beneficiaries, skills, livelihoods, photo)
//                    profilesWithDetails.add(profileWithDetails)
//                }
//            }
//        } catch (e: Exception) {
//            // Handle any exceptions here
//            e.printStackTrace()
//        } finally {
//            cursor?.close()
//            db?.close()
//        }
//
//        return profilesWithDetails
//    }

    }