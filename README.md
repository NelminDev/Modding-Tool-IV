# ND Modding Tool IV

A comprehensive modding tool for Grand Theft Auto IV, designed to simplify the process of installing, managing, and
creating mods.

## Features

### Mod Management

- List all installed mods with options to uninstall or update
- Browse and install mods compatible with your game version
- Directly download mods from trusted sources
- Option to update mod index
- Option to change game instance

### Mod Creation

- Support for creating .rpf and .img files (similar to OpenIV)
- Custom (archive structure)[#Archive-Structure] support (zip/tar.gz/etc.)

### Backup System

- Automatic backup of original files when modifying base game files
- Option to load specific backups
- Option to create new backups

### Fusion Overloader Support

- Native support for Fusion Overloader
- Option to create Fusion Overloader "patches" for mods that don't natively support it

### Archive Structure

Mods should follow this structure:

```
- infos/ (readme, ...)
- files/ (must mirror base game files)
    - example: /files/pc/common/x/x/x.img/ (folder **not** the file with .img extension)
- nd-mt.iv.json
    - name
    - version
    - author
    - description
    - website
    - source code
    - game versions (list, values: 1.0.4.0, 1.0.7.0, 1.0.8.0, 1.2.0.59)
```

## System Requirements

- Windows operating system (currently MTIV does not support other platforms)
- Compatible with GTA IV versions:
    - 1.0.4.0
    - 1.0.7.0
    - 1.0.8.0
    - 1.2.0.59 (Complete Edition/EFLC)

## Installation

1. Download the latest release from the releases page
2. Run the application
3. On first run, you'll be prompted to select your GTAIV.exe location
4. The application will automatically configure itself for your game version

## Configuration

The application stores its configuration in a `config.json` file in the application directory. This includes:

- Default game version
- Game installation directories for different versions

## Planned Features

- Mod uploading functionality (when REST API is implemented)
- Mod rating system (similar to ProtonDB) for rating functionality on specific game versions (when REST API is
  implemented)

## Credits

Developed by Nelmin Development
