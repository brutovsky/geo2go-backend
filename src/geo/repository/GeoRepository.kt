package com.brtvsk.geo.repository

import com.brtvsk.auth.models.VisitedGeo
import com.brtvsk.auth.repository.DatabaseFactory
import com.brtvsk.auth.repository.tables.VisitedGeos
import com.brtvsk.geo.MongoDB.MongoDB
import com.brtvsk.geo.models.FavTag
import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point
import com.mongodb.client.FindIterable
import org.bson.types.ObjectId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.litote.kmongo.*
import org.litote.kmongo.id.ObjectIdGenerator

class GeoRepository:Repository{

    private val geo = MongoDB.mongoDB.getCollection<Geo>(Geos.COL_GEOS)

    override suspend fun addGeo(userId:Int, point: Point,
                                type: GeoType, tags: Set<Int>,
                                description: String): Geo? {
        val id = ObjectIdGenerator.newObjectId<Geo>().id
        geo.insertOne(Geo(id,userId,point,type,tags, description))
        return geo.findOne(Geo::_id eq id)
    }

    override suspend fun findGeo(point:Point): Geo? {
        return geo.findOne(Geo::point eq point)
    }

    override suspend fun findGeo(geoId: ObjectId): Geo? {
        return geo.findOne(Geo::_id eq geoId)
    }

    override suspend fun findGeosByIds(geoIds: List<ObjectId>) = geo.find(Geo::_id `in` geoIds).toList()

    override suspend fun getAll(): List<Geo>{
        val found = geo.find()
        return found.toList()
    }

    override suspend fun incrementVisitedGeo(userId:Int, geoId: String) : Int {
        var result : Int = 0
        DatabaseFactory.dbQuery {
            result = VisitedGeos.update({ (VisitedGeos.userId eq userId) and (VisitedGeos.geoId eq geoId) }) {
                with(SqlExpressionBuilder) {
                    it.update(counter, counter + 1)
                }
            }
        }
        return result
    }

    override suspend fun addVisitedGeo(userId:Int, geoId: String) : VisitedGeo? {
        var statement : InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = VisitedGeos.insert {
                it[VisitedGeos.userId] = userId
                it[VisitedGeos.geoId] = geoId
                it[counter] = 1
            }
        }
        return rowToVisitedGeo(statement?.resultedValues?.get(0))
    }

    override suspend fun findVisitedGeo(userId: Int, geoId: String) = DatabaseFactory.dbQuery {
        VisitedGeos.select { (VisitedGeos.userId eq userId) }
            .map { rowToVisitedGeo(it) }.singleOrNull()
    }

    override suspend fun getAllVisited(userId: Int) = DatabaseFactory.dbQuery {
        VisitedGeos.select { (VisitedGeos.userId eq userId) }
            .mapNotNull { rowToVisitedGeo(it) }
    }

    override suspend fun getNearGeos(userId: Int, geoType:String, point:Point, range: IntRange): FindIterable<Geo> {
        return geo.find("""
            db.Geo.find(
               {
                 type: { ${MongoOperator.eq}: $geoType } ,
                 userId: { ${MongoOperator.ne}: $userId } ,
                 point:
                   { ${MongoOperator.near} :
                      {
                                    "$ geometry" : { type: "Point",  coordinates: [ ${point.coordinates[0]}, ${point.coordinates[1]} ] },
                                    "$ minDistance" : ${range.first},
                                    "$ maxDistance" : ${range.last}
                      }
                   }
               }
            )
        """.trimIndent())
    }

    private fun rowToVisitedGeo(row: ResultRow?): VisitedGeo?{
        if (row == null) {
            return null
        }
        return VisitedGeo(
            userId = row[VisitedGeos.userId],
            geoId = row[VisitedGeos.geoId],
            counter = row[VisitedGeos.counter],
        )
    }

}