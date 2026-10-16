const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profile or events", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("events").get());
});

test("Authenticated user: can create and read their own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@test.com",
      createdAt: new Date(),
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read or write another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Bob Overwrite",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: can create and read their own events", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("events").doc("event_1").set({
      userId: ALICE_UID,
      name: "Mom Birthday",
      dateDay: 15,
      dateMonth: 8,
      birthYear: 1968,
      eventType: "BIRTHDAY",
      relationship: "FAMILY",
      notes: "Buy flowers",
      isReminderEnabled: true,
      reminderDaysBefore: 1,
      reminderHour: 9,
      reminderMinute: 0,
      createdAt: new Date(),
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("events").doc("event_1").get()
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("events").get()
  );
});

test("Authenticated user: cannot read or write another user's events", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("events").doc("event_1").set({
      userId: ALICE_UID,
      name: "Alice Secret Event",
      dateDay: 10,
      dateMonth: 5,
      eventType: "BIRTHDAY",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("events").doc("event_1").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("events").doc("event_1").delete()
  );
});

test("Schema validation: rejects event with invalid day or missing required fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Invalid day 45
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("events").doc("event_bad").set({
      userId: ALICE_UID,
      name: "Bad Date",
      dateDay: 45,
      dateMonth: 8,
      eventType: "BIRTHDAY",
      createdAt: new Date(),
    })
  );
});
