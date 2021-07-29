package de.fraunhofer.iosb.svs.analysishub.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String item, String uri) {
        super("No '" + item + "' with uri '" + uri + "' found");
    }

    public ResourceNotFoundException(String item, Long id) {
        super("No '" + item + "' with id '" + id + "' found");
    }

    public ResourceNotFoundException(String customMessage) {
        super(customMessage);
    }
}
