package net.cpollet.seles.impl.testsupport;

import java.security.Principal;

public class VoidPrincipal implements Principal{
    @Override
    public String getName() {
        return "";
    }
}
