# Phase 1: SoftFloat (39) + AsyncTask (3) + ScreenBuilder (4)

# SoftFloat methods
s/\bm675l\b/isNegative/g
s/\bm676m\b/getExponent/g
s/\bm677n\b/getMantissa/g
s/\bm678a\b/packFloat/g
s/\bm679o\b/isNaN/g
s/\bm680p\b/isInfinite/g
s/\bm681q\b/isZero/g
s/\bm682a\b/negate/g
s/\bm683g\b/lessOrEqual/g
s/\bm684h\b/greaterOrEqual/g
s/\bm685a\b/compare/g
s/\bm686i\b/compareRaw/g
s/\bm687b\b/longToFloat/g
s/\bm688c\b/floatToInt/g
s/\bm689d\b/floatToLong/g
s/\bm690b\b/add/g
s/\bm691c\b/subtract/g
s/\bm692d\b/multiply/g
s/\bm693e\b/divide/g
s/\bm694a\b/roundToInt/g
s/\bm695b\b/decimalToFloat/g
s/\bm696j\b/multiplyHigh/g
s/\bm697a\b/parseFloat/g
s/\bm698a\b/formatFloat/g
s/\bm699e\b/reciprocal/g
s/\bm700b\b/packLowInt/g
s/\bm701k\b/copySign/g
s/\bm702c\b/scalb/g
s/\bm703f\b/exp/g
s/\bm704f\b/pow/g
s/\bm705g\b/log/g
s/\bm706h\b/sin/g
s/\bm707i\b/cos/g
s/\bm708a\b/tanKernel/g
s/\bm709b\b/sinKernel/g
s/\bm710l\b/cosKernel/g
s/\bm711a\b/reduceArg/g
s/\bm712j\b/atan/g
s/\bm713k\b/cosFull/g

# AsyncTask fields
s/\bf434b\b/taskId/g
s/\bf435c\b/taskData/g
s/\bf436a\b/thread/g

# ScreenBuilder methods
s/\bm546a\b/openScreen/g
s/\bm547a\b/onMenuItemSelected/g
s/\bm548b\b/onMenuItemAction/g
s/\bm549c\b/onScreenClosed/g
