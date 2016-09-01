package com.softmotions.ncms.db

import com.softmotions.ncms.DbTestsFactory

/**
 * @author Adamansky Anton (adamansky@gmail.com)
 */
class TestDBs : DbTestsFactory() {

    override fun createTest(db: String): Array<Any> {
        return arrayOf(
                _TestAsmDAO(db),
                _TestAsmRSDB(db),
                _TestAsmMergeQueries(db)
                )
    }

}