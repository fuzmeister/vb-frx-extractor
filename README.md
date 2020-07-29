# vb-frx-extractor
A tool to extract image resources stored in Visual Basic FRX files. Currently, the utility only has the ability to extract BMP, JPG, GIF, and ICO images.

This tool is provided as-is and was written several years ago as a quick and dirty solution to retrieve images from an old project written in **VB5**. I am not familiar enough with the proprietary FRX format used by VB, so no guarantees this will work with files created using other versions of Visual Basic.

## Usage
**FormFileProcessor** expects 2 + n arguments: an extraction path (E.G. /home/fuzmeister/) followed by the absolute path of at least one Visual Basic .frm file. This was written very lazily and will require some modifications if you want it to work in Windows. ;)

## Changelog
2020-07-29
- Added support for VB PictureBox controls. Added static method to FRXResource class to retrieve file type based on provided byte. General clean-up.

2020-07-22
- Initial commit of files
