package Model.Person;

public class ObjectCurrent {
    //Static use to allow administrator access everywhere
    private static Object ObjectCurrent;
    public static Object getObjectCurrent() {
        return ObjectCurrent;
    }

    public static void setObjectCurrent(Object oc) {
        ObjectCurrent = oc;
    }
}
