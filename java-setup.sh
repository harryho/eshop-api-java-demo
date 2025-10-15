#!/bin/bash

# Java environment setup script
# This script helps manage Java versions on macOS

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to list all installed Java versions
list_java_versions() {
    echo -e "${YELLOW}Installed Java versions:${NC}"
    /usr/libexec/java_home -V 2>&1 | grep -v "Matching Java Virtual Machines"
    echo ""
}

# Function to show current Java version
show_current() {
    echo -e "${YELLOW}Current Java configuration:${NC}"
    echo "JAVA_HOME: ${JAVA_HOME:-<not set>}"
    echo ""
    if command -v java &> /dev/null; then
        echo -e "${GREEN}Java version:${NC}"
        java -version 2>&1
    else
        echo -e "${RED}Java command not found${NC}"
    fi
}

# Function to set Java version
set_java_version() {
    local version=$1
    
    if [ -z "$version" ]; then
        echo -e "${RED}Error: Please specify a Java version (e.g., 21, 17, 11)${NC}"
        exit 1
    fi
    
    # Try to find the Java home for the specified version
    local java_home_path
    java_home_path=$(/usr/libexec/java_home -v "$version" 2>/dev/null)
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Error: Java version $version not found${NC}"
        echo ""
        list_java_versions
        exit 1
    fi
    
    echo -e "${GREEN}Setting JAVA_HOME to Java $version${NC}"
    echo "Path: $java_home_path"
    
    # Update current session
    export JAVA_HOME="$java_home_path"
    export PATH="$JAVA_HOME/bin:$PATH"
    
    # Update .zshrc for future sessions
    if [ -f ~/.zshrc ]; then
        # Backup .zshrc
        cp ~/.zshrc ~/.zshrc.backup.$(date +%Y%m%d_%H%M%S)
        
        # Remove old JAVA_HOME lines
        grep -v "export JAVA_HOME=" ~/.zshrc > ~/.zshrc.tmp || true
        
        # Add new JAVA_HOME
        echo "" >> ~/.zshrc.tmp
        echo "# Java $version - Updated by java-setup.sh on $(date)" >> ~/.zshrc.tmp
        echo "export JAVA_HOME=\"$java_home_path\"" >> ~/.zshrc.tmp
        echo "export PATH=\"\$JAVA_HOME/bin:\$PATH\"" >> ~/.zshrc.tmp
        
        mv ~/.zshrc.tmp ~/.zshrc
        
        echo -e "${GREEN}Updated ~/.zshrc${NC}"
    fi
    
    echo ""
    echo -e "${GREEN}✓ Java environment updated successfully!${NC}"
    echo ""
    show_current
    echo ""
    echo -e "${YELLOW}Note: Run 'source ~/.zshrc' or restart your terminal for changes to take effect in other terminals${NC}"
}

# Function to fix Java for this project
fix_for_project() {
    echo -e "${YELLOW}Fixing Java environment for eshop-api-java-demo...${NC}"
    echo ""
    echo "This project requires Java 21 (as specified in build.gradle)"
    echo ""
    
    # Check if Java 21 is available
    if /usr/libexec/java_home -v 21 &> /dev/null; then
        set_java_version 21
    else
        echo -e "${RED}Error: Java 21 is not installed${NC}"
        echo ""
        echo "Please install Java 21 using one of these methods:"
        echo "  - Homebrew: brew install openjdk@21"
        echo "  - SDKMAN: sdk install java 21.0.7-ms"
        echo "  - Download from: https://www.oracle.com/java/technologies/downloads/"
        exit 1
    fi
}

# Main script
show_usage() {
    echo "Usage: $0 {list|current|set <version>|fix}"
    echo ""
    echo "Commands:"
    echo "  list           - List all installed Java versions"
    echo "  current        - Show current Java version and JAVA_HOME"
    echo "  set <version>  - Set Java version (e.g., ./java-setup.sh set 21)"
    echo "  fix            - Auto-configure Java 21 for this project"
    echo ""
    echo "Examples:"
    echo "  $0 list                # List all installed Java versions"
    echo "  $0 current             # Show current Java configuration"
    echo "  $0 set 21              # Switch to Java 21"
    echo "  $0 fix                 # Fix Java for this project (sets Java 21)"
    echo ""
}

# Parse command
case "${1:-}" in
    list)
        list_java_versions
        ;;
    current)
        show_current
        ;;
    set)
        set_java_version "$2"
        ;;
    fix)
        fix_for_project
        ;;
    *)
        show_usage
        exit 1
        ;;
esac

exit 0
