# TODO - Verify OTP request payload fix

- [ ] Update `VerifyOtpTest` verify payload to use `workspaceID` (capital “ID”) instead of `workspaceId`.
- [x] Ensure `workspaceID` value is `null` when `Config.WORKSPACE_ID` is unset/blank.

- [ ] Run `mvn test` (or specific VerifyOtpTest) to confirm build passes.

