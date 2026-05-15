package common;

public interface CommandeListener {
    void onValidated(String uuid);
    void onDelivery(String uuid, String serials);
    void onCancelled(String uuid, String reason);
    void onError(String uuid, String error);
}
