import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

const html = readFileSync(new URL("../app/src/main/assets/index.html", import.meta.url), "utf8");
const activity = readFileSync(
  new URL("../app/src/main/java/me/stefk/gordianknot/MainActivity.java", import.meta.url),
  "utf8"
);

test("short viewports get compact spacing without smaller controls", () => {
  assert.match(html, /@media\s*\(max-height:\s*800px\)/);
  assert.match(html, /min-height:\s*48px/);
  assert.match(html, /align-items:\s*start/);
});

test("secure-window protection remains enabled for release builds", () => {
  assert.match(activity, /ApplicationInfo\.FLAG_DEBUGGABLE/);
  assert.match(activity, /WindowManager\.LayoutParams\.FLAG_SECURE/);
});
