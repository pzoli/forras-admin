package hu.exprog.beecomposit.utils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JAXBLongAdapter extends XmlAdapter<String, Long> {
    /** {@inheritDoc} */
    @Override
    public Long unmarshal(String v) throws Exception {
        return Long.parseLong(v);
    }

    /** {@inheritDoc} */
    @Override
    public String marshal(Long v) throws Exception {
        return Long.toString(v);
    }
}
