package hu.infokristaly.utils;

import java.util.Comparator;

import hu.infokristaly.back.domain.Client;

public class ClientComparatorByName implements Comparator<Client> {

    @Override
    public int compare(Client o1, Client o2) {
        int result = 0;
        if ((o1 != null) && (o2 != null) && (o1.getNeve() != null)) {
            result = o1.getNeve().compareToIgnoreCase(o2.getNeve());
        }
        return result;
    }

}
