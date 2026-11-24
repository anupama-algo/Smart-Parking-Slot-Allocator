# Smart Parking Slot Allocator — Testing Evidence

## Database Integration Testing

The application was tested using MySQL 8.0 and Java JDBC.

### Verified Test Scenarios

| Test | Result |
|---|---|
| Vehicle entry and automatic slot allocation | Passed |
| Vehicle exit and slot release | Passed |
| Duplicate active vehicle prevention | Passed |
| Full parking area rejection | Passed |
| Incompatible vehicle category rejection | Passed |
| Invalid vehicle exit handling | Passed |
| Parking fee calculation | Passed |
| Occupancy and availability reports | Passed |
| Daily collection report | Passed |

### Recorded Test Sessions

| Session ID | Vehicle Number | Entry Time | Exit Time | Fee |
|---|---|---|---|---:|
| 2 | PB10AB1234 | 2026-11-23 23:55:25 | 2026-11-24 00:01:50 | ₹20 |
| 3 | PB10AB1234 | 2026-11-24 00:38:26 | Active at cleanup | — |
| 6 | PB10TEST01 | 2026-11-24 00:43:59 | Active at cleanup | — |
| 7 | PB10TEST02 | 2026-11-24 00:44:09 | Active at cleanup | — |
| 8 | PB10BIKE01 | 2026-11-24 09:00:08 | 2026-11-24 10:02:43 | ₹30 |

### Cleanup Notes

The Demo Parking area was used for integration and functional testing.

Its test records are documented here before removal from the live demonstration database. The three active test sessions were not actual customer bookings.

The test results demonstrate vehicle allocation, duplicate prevention, vehicle exit processing, fee calculation and database reporting.

