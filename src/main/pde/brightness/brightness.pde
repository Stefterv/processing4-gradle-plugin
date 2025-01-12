/**
 * Brightness
 * by Rusty Robison.
 *
 * Brightness is the relative lightness or darkness of a color.
 * Move the cursor vertically over each bar to alter its brightness.
 */
import com.google.gson.*;

int barWidth = 20;
int lastBar = -1;


void setup() {
  size(640, 360);
  colorMode(HSB, width, 100, height);
  noStroke();
  background(0);

  Gson gson = new Gson();
  println(Gson.class);

  String[] file = loadStrings("input.txt");
  println(file);
}

void draw() {
  int whichBar = mouseX / barWidth;
  if (whichBar != lastBar) {
    int barX = whichBar * barWidth;
    fill(barX, 100, mouseY);
    rect(barX, 0, barWidth, height);
    lastBar = whichBar;
  }
}