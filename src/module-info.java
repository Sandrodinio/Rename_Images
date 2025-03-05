
module test {
	requires javafx.controls;
	requires javafx.graphics;
	requires java.logging;	
	opens application to javafx.graphics, javafx.fxml;
}
