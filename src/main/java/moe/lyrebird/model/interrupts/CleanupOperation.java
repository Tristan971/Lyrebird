package moe.lyrebird.model.interrupts;

public final class CleanupOperation {

    private final String name;
    private final Runnable operation;

    public CleanupOperation(String name, Runnable operation) {
        this.name = name;
        this.operation = operation;
    }

    public String getName() {
        return name;
    }

    public Runnable getOperation() {
        return operation;
    }

}
