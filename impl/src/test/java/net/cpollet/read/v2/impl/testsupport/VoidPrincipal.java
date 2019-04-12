package net.cpollet.read.v2.impl.testsupport;

import java.security.Principal;

public class VoidPrincipal implements Principal{
    @Override
    public String getName() {
        return "";
    }
}
