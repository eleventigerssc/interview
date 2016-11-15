package io.github.eleventigerssc.interview.streams;

class SystemLogger implements Logger {

    @Override
    public void log(Object o) {
        System.out.println(o);
    }
}
