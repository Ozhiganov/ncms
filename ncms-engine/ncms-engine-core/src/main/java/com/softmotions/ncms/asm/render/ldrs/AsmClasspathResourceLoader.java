package com.softmotions.ncms.asm.render.ldrs;

import httl.spi.loaders.ClasspathLoader;
import com.softmotions.ncms.asm.render.AsmResourceLoader;
import com.softmotions.ncms.media.MediaResource;

import com.google.inject.Singleton;

import java.io.IOException;
import java.util.Locale;

/**
 * @author Adamansky Anton (adamansky@gmail.com)
 */
@Singleton
public class AsmClasspathResourceLoader implements AsmResourceLoader {

    private final ClasspathLoader loader;

    public AsmClasspathResourceLoader() {
        this.loader = new ClasspathLoader();
    }

    public boolean exists(String name, Locale locale) {
        return loader.exists(name, locale);
    }

    public MediaResource load(String name, Locale locale) throws IOException {
        return new HttlMediaResourceAdapter(loader.load(name, locale, null));
    }
}

