open module firok.tool.tinwrench {
	requires lombok;
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires org.kordamp.ikonli.javafx;

	requires alloy.wrench;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
//	requires com.fasterxml.jackson.annotation;
//	requires org.graalvm.truffle;

	requires firok.topaz;
	requires java.desktop;

//	opens firok.tool.tinwrench to javafx.fxml;
	exports firok.tool.tinwrench.main;
	exports firok.tool.tinwrench.selectcoco;
//	opens firok.tool.tinwrench.main to javafx.fxml;

}
