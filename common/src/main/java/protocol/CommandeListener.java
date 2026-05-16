package protocol;

public interface CommandeListener {

    default void onValidated(String uuid) {
    }

    default void onDelivery(String uuid, String serials) {
    }

    default void onCancelled(String uuid, String reason) {
    }

    default void onError(String uuid, String error) {
    }

    default void onChecked(String uuid, String typeLunette){}
}
