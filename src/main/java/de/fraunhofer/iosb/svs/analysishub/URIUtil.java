package de.fraunhofer.iosb.svs.analysishub;

public class URIUtil {

    /**
     * Get the index to split the Uri into namespace and local name.
     * <p>
     * From https://github.com/eclipse/rdf4j/blob/3.6.0/core/model/src/main/java/org/eclipse/rdf4j/model/util/URIUtil.java
     *
     * @param uri
     * @return
     */
    public static int getLocalNameIndex(String uri) {
        int separatorIdx = uri.indexOf('#');

        if (separatorIdx < 0) {
            separatorIdx = uri.lastIndexOf('/');
        }

        if (separatorIdx < 0) {
            separatorIdx = uri.lastIndexOf(':');
        }

        if (separatorIdx < 0) {
            throw new IllegalArgumentException("No separator character founds in URI: " + uri);
        }

        return separatorIdx + 1;
    }

    public static String getLocalName(String uri) {
        int localNameIdx = URIUtil.getLocalNameIndex(uri);
        return uri.substring(localNameIdx);
    }

    public static String getNamespace(String uri) {
        int localNameIdx = URIUtil.getLocalNameIndex(uri);
        return uri.substring(0, localNameIdx);
    }

    public static String assemble(String namespace, String localName) {
        return namespace + localName;
    }
}
