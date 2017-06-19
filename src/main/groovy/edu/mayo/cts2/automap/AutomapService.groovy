package edu.mayo.cts2.automap
import edu.mayo.cts2.framework.core.json.JsonConverter
import edu.mayo.cts2.framework.model.core.MapVersionReference
import edu.mayo.cts2.framework.model.core.NameAndMeaningReference
import edu.mayo.cts2.framework.model.mapversion.*
import edu.mayo.cts2.framework.model.valuesetdefinition.IteratableResolvedValueSet
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component

import java.util.concurrent.Executors

@Component
class AutomapService implements DisposableBean {

    def threadPool = Executors.newFixedThreadPool(4)

    def jsonConverter = new JsonConverter()

    @Override
    void destroy() throws Exception {
        threadPool.shutdown()
    }

    def map(mapVersionHref, maxDistance) {
        MapVersion mapVersion = jsonConverter.fromJson(getJson(mapVersionHref)).mapVersion

        IteratableResolvedValueSet fromVsd
        IteratableResolvedValueSet toVsd

        threadPool.invokeAll([
                {-> fromVsd = jsonConverter.fromJson(
                        getJson(mapVersion.fromValueSetDefinition.valueSetDefinition.href + "/resolution"))},
                {-> toVsd = jsonConverter.fromJson(
                        getJson(mapVersion.toValueSetDefinition.valueSetDefinition.href + "/resolution"))}
        ])

        def list = new MapEntryList()

        def assertedBy = new MapVersionReference(
            map: mapVersion.versionOf,
            mapVersion: new NameAndMeaningReference(
                    content: mapVersion.getMapVersionName(),
                    uri: mapVersion.getAbout(),
                    href: mapVersionHref
            )
        )

        fromVsd.entry.each { from ->
            toVsd.entry.each { to ->
                def distance = getDistance(from, to)

                if (distance <= maxDistance) {
                    list.addEntry(new MapEntryListEntry(
                            entry: new MapEntry(
                                    assertedBy: assertedBy,
                                    mapFrom: from,
                                    mapSet: [new MapSet(mapTarget: [new MapTarget(mapTo: to)] as MapTarget[])] as MapSet[])))
                }
            }
        }

        list
    }

    def getJson(href) {
        href.toURL().
                getText(requestProperties: [Accept: 'application/json'])
    }

    def getDistance(entity1, entity2) {
        StringUtils.getLevenshteinDistance(entity1.designation, entity2.designation)
    }
}
