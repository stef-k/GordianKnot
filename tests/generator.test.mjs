import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import vm from "node:vm";
import { webcrypto } from "node:crypto";

const html = readFileSync(new URL("../app/src/main/assets/index.html", import.meta.url), "utf8");
const script = html.match(/<script>([\s\S]*?)<\/script>/)?.[1];
assert.ok(script, "embedded generator script should exist");

function element(overrides = {}) {
  return {
    value: "",
    checked: false,
    type: "text",
    textContent: "",
    className: "",
    dataset: {},
    classList: { add() {} },
    addEventListener() {},
    focus() {},
    select() {},
    ...overrides
  };
}

const elements = new Map([
  ["length", element({ value: "20" })],
  ["theme", element({ value: "system" })],
  ["lower", element({ checked: true })],
  ["upper", element({ checked: true })],
  ["digits", element({ checked: true })],
  ["password", element({ type: "password" })],
  ["show", element()],
  ["generate", element()],
  ["copy", element()],
  ["status", element()],
  ["strength", element()]
]);
const symbol = element({ value: "basic", checked: true });
const context = vm.createContext({
  crypto: webcrypto,
  document: {
    documentElement: element(),
    getElementById: id => elements.get(id),
    querySelector: selector => selector.includes("symbols") ? symbol : null,
    querySelectorAll: selector => selector.includes("symbols") ? [symbol] : []
  },
  navigator: { clipboard: { async writeText() {} } },
  window: {
    matchMedia: () => ({ matches: false, addEventListener() {} })
  }
});

vm.runInContext(script, context);

for (let iteration = 0; iteration < 200; iteration += 1) {
  vm.runInContext("generatePassword()", context);
  const password = elements.get("password").value;
  assert.equal(password.length, 20);
  assert.match(password, /[a-z]/);
  assert.match(password, /[A-Z]/);
  assert.match(password, /[0-9]/);
  assert.match(password, /[!@#$%]/);
}

vm.runInContext("els.lower.checked = els.upper.checked = els.digits.checked = false", context);
symbol.value = "none";
vm.runInContext("generatePassword()", context);
assert.equal(elements.get("password").value, "");
assert.equal(elements.get("status").textContent, "Enable at least one character group.");

console.log("Generator tests passed.");
