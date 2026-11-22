
module Asac {

	requires javafx.controls;
	requires transitive javafx.graphics;
	requires java.prefs;
	requires java.base;
	requires java.logging;
	requires javafx.media;

	exports com.ralf.asac;
	exports com.ralf.asac.test;
}
