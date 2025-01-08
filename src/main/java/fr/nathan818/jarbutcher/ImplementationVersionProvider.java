package fr.nathan818.jarbutcher;

import picocli.CommandLine;

class ImplementationVersionProvider implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[] { App.class.getPackage().getImplementationVersion() };
    }
}
