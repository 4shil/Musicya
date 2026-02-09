#!/bin/bash
# Musicya Build Verification Script
# Run this to verify the build is ready for release

set -e

echo "=== Musicya Build Verification ==="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check for required tools
echo -e "${YELLOW}Checking prerequisites...${NC}"

command -v java >/dev/null 2>&1 || { echo -e "${RED}Java is required but not installed.${NC}" >&2; exit 1; }
command -v gradle >/dev/null 2>&1 || { echo -e "${RED}Gradle is required but not installed.${NC}" >&2; exit 1; }

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
echo "  Java version: $JAVA_VERSION"

# Check Gradle version
GRADLE_VERSION=$(./gradlew --version 2>/dev/null | head -3 | tail -1 | awk '{print $3}')
echo "  Gradle version: $GRADLE_VERSION"

echo ""
echo -e "${YELLOW}Running static analysis...${NC}"

# Check for TODO/FIXME comments
echo "  Checking for TODO/FIXME comments..."
TODO_COUNT=$(grep -r "TODO\|FIXME\|XXX" app/src/main/java 2>/dev/null | wc -l)
if [ "$TODO_COUNT" -gt 0 ]; then
    echo -e "${YELLOW}  Found $TODO_COUNT TODO/FIXME comments (acceptable for development)${NC}"
fi

# Check file structure
echo "  Verifying project structure..."
REQUIRED_DIRS=(
    "app/src/main/java/com/fourshil/musicya/ui"
    "app/src/main/java/com/fourshil/musicya/data"
    "app/src/main/java/com/fourshil/musicya/player"
    "app/src/main/java/com/fourshil/musicya/util"
    "app/src/main/res/layout"
    "app/src/main/res/values"
    "app/src/test/java"
    "app/src/androidTest/java"
)

for dir in "${REQUIRED_DIRS[@]}"; do
    if [ -d "$dir" ]; then
        echo -e "    ${GREEN}✓${NC} $dir"
    else
        echo -e "    ${RED}✗${NC} $dir (missing)"
    fi
done

echo ""
echo -e "${YELLOW}Building debug APK...${NC}"
./gradlew assembleDebug --no-daemon --quiet

if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    APK_SIZE=$(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)
    echo -e "  ${GREEN}✓${NC} Debug APK built successfully ($APK_SIZE)"
else
    echo -e "  ${RED}✗${NC} Debug APK not found"
    exit 1
fi

echo ""
echo -e "${YELLOW}Running unit tests...${NC}"
./gradlew test --no-daemon --quiet || {
    echo -e "${YELLOW}  Some tests failed (check above for details)${NC}"
}

echo ""
echo -e "${YELLOW}Building release APK...${NC}"
./gradlew assembleRelease --no-daemon --quiet || {
    echo -e "${RED}  Release build failed. Check configuration.${NC}"
    exit 1
}

if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    APK_SIZE=$(du -h app/build/outputs/apk/release/app-release.apk | cut -f1)
    echo -e "  ${GREEN}✓${NC} Release APK built successfully ($APK_SIZE)"
else
    echo -e "  ${YELLOW}⚠${NC} Release APK not found (may need signing config)"
fi

echo ""
echo -e "${GREEN}=== Build Verification Complete ===${NC}"
echo ""
echo "Next steps:"
echo "  1. Review test results"
echo "  2. Sign release APK: ./gradlew signRelease"
echo "  3. Verify APK with: zipalign -v -c app/build/outputs/apk/release/app-release.apk"
echo "  4. Upload to Play Store"