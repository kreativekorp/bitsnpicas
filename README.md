# Bits'N'Picas

Bits'N'Picas is a set of tools for creating and converting bitmap and emoji fonts.

## Creating and Editing Bitmap Fonts with a GUI

Launch the Bits'N'Picas JAR without any arguments or with the `edit` command to open the bitmap font editor GUI.

`java -jar BitsNPicas.jar`

`java -jar BitsNPicas.jar edit`

`java -jar BitsNPicas.jar edit myfont.sfd`

The input format is determined by the file extension of the input file. Supported input formats include:
  *  `.kbits` - Bits'N'Picas native save format
  *  `.sfd` - FontForge
  *  `.bdf` - X11 Bitmap Distribution Format
  *  `.suit` - Mac OS Classic font suitcase (in the resource fork)
  *  `.dfont` - Mac OS Classic font suitcase (in the data fork)
  *  `.png` - SFont or RFont, Kreative Software's extension of SFont
  *  `.png`, `.jpg`, `.jpeg`, `.gif`, `.bmp` - Create from image (GUI only)
  *  `.bin`, `.rom` - Create from binary file (GUI only)
  *  `.hex` - [GNU Unifont](http://unifoundry.com/unifont/index.html) hex format
  *  `.fzx` - [FZX by Andrew Owen (for ZX Spectrum)](https://faqwiki.zxnet.co.uk/wiki/FZX_format)
  *  `.u8m` - [U8/M (UTF-8 for Microcomputers)](https://github.com/kreativekorp/u8m)
  *  `.hmzk` - [Mi Band 2 Font Format](https://github.com/Freeyourgadget/Gadgetbridge/wiki/Mi-Band-2-%28HMZK%29-Font-Format)
  *  `.dsf` - [DOSStart! by Daniel L. Nice](https://web.archive.org/web/20120209004900/http://www.icdc.com/~dnice/dosstart.html)
  *  `.sbf` - Sabriel Bitmap Font

On Mac OS X you can also launch or drop a font file onto the Bits'N'Picas application.

![](https://github.com/kreativekorp/bitsnpicas/blob/master/wiki/screenshot.png?raw=true)

## Converting Bitmap Fonts

Example:

`java -jar BitsNPicas.jar convertbitmap -f ttf -o myfont.ttf myfont.sfd`

This will convert the bitmap strikes in the FontForge file `myfont.sfd` to outlines in a new TrueType font file `myfont.ttf`. If, for example, the bitmap strikes are 16 pixels in height, the generated outlines will perfectly match the pixel grid at a 16-point font size.

The input format is determined by the file extension of the input file. Supported input formats include:
  *  `.kbits` - Bits'N'Picas native save format
  *  `.sfd` - FontForge
  *  `.bdf` - X11 Bitmap Distribution Format
  *  `.suit` - Mac OS Classic font suitcase (in the resource fork)
  *  `.dfont` - Mac OS Classic font suitcase (in the data fork)
  *  `.png` - SFont or RFont, Kreative Software's extension of SFont
  *  `.hex` - [GNU Unifont](http://unifoundry.com/unifont/index.html) hex format
  *  `.fzx` - [FZX by Andrew Owen (for ZX Spectrum)](https://faqwiki.zxnet.co.uk/wiki/FZX_format)
  *  `.u8m` - [U8/M (UTF-8 for Microcomputers)](https://github.com/kreativekorp/u8m)
  *  `.hmzk` - [Mi Band 2 Font Format](https://github.com/Freeyourgadget/Gadgetbridge/wiki/Mi-Band-2-%28HMZK%29-Font-Format)
  *  `.dsf` - [DOSStart! by Daniel L. Nice](https://web.archive.org/web/20120209004900/http://www.icdc.com/~dnice/dosstart.html)
  *  `.sbf` - Sabriel Bitmap Font

The output format is determined by the `-f` option. Supported output formats include:
  *  `kbits` or `kbnp` - Bits'N'Picas native save format
  *  `ttf` or `truetype` - TrueType
  *  `bdf` - X11 Bitmap Distribution Format
  *  `nfnt` or `suit` - Mac OS Classic font suitcase (in the resource fork)
  *  `dfont` - Mac OS Classic font suitcase (in the data fork)
  *  `png` or `sfont` - SDL SFont
  *  `rfont` - RFont, Kreative Software's extension of SFont
  *  `hex` - [GNU Unifont](http://unifoundry.com/unifont/index.html) hex format
  *  `fzx` - [FZX by Andrew Owen (for ZX Spectrum)](https://faqwiki.zxnet.co.uk/wiki/FZX_format)
  *  `u8m` - [U8/M (UTF-8 for Microcomputers)](https://github.com/kreativekorp/u8m)
  *  `hmzk` - [Mi Band 2 Font Format](https://github.com/Freeyourgadget/Gadgetbridge/wiki/Mi-Band-2-%28HMZK%29-Font-Format)
  *  `sbf` - Sabriel Bitmap Font

Additional options include:
  *  `-s` *regex* `-r` *replacement* - Perform a search-and-replace on the font name.
  *  `-b` - Apply a faux-bold effect.
  *  `-w` *units* `-h` *units* - Specify the width and height of pixels in em units (for `ttf` format).
  *  `-i` *fontid* `-z` *size* - Specify the Macintosh font ID and font size (for `nfnt` format).

## Extracting Images from Emoji/Color Fonts

### Example using Apple's `sbix` format

`java -jar BitsNPicas.jar extractsbix AppleColorEmoji.ttf`

This will extract the images from `AppleColorEmoji.ttf` into the directory `AppleColorEmoji.ttf.sbix.d` as PNG files. There will be several numbered subdirectories within this directory; the number corresponds to the *pixels per em* of the images within. (This is not necessarily the same thing as the image height!) The resulting directory structure will look like:

```
  AppleColorEmoji.ttf
  AppleColorEmoji.ttf.sbix.d
      20
          glyph_43.png
          glyph_44.png
          ...
      32
          glyph_43.png
          glyph_44.png
          ...
      ...
```

### Example using Adobe and Mozilla's SVG-in-OpenType format

`java -jar BitsNPicas.jar extractsvg EmojiOne.otf`

This will extract the SVG images from `EmojiOne.otf` into the directory `EmojiOne.otf.svg.d` as uncompressed SVG files. (If the SVG data inside the font is compressed, it will be decompressed.) The resulting directory structure will look like:

```
  EmojiOne.otf
  EmojiOne.otf.svg.d
      glyph_2.svg
      glyph_3.svg
      ...
```

### Example using Google's `CBDT`/`CBLC` format

`java -jar BitsNPicas.jar extractcbdt NotoColorEmoji.ttf`

This will extract the PNG images from `NotoColorEmoji.ttf` into the directory `NotoColorEmoji.ttf.cbdt.d`. There will be one or more numbered subdirectories within this directory; the number is simply the index of the BitmapSize record and has no other significance. The resulting directory structure will look like:

```
  NotoColorEmoji.ttf
  NotoColorEmoji.ttf.cbdt.d
      0000
          glyph_4.png
          glyph_5.png
          ....
          metadata.txt
      ....
      fontinfo.txt
```

The `metadata.txt` file within each subdirectory lists all the values from the various data structures within the `CBLC` and `CBDT` tables. The `fontinfo.txt` file just lists some other values from other, unrelated tables for convenience (since many font editors cannot open fonts with `CBDT`/`CBLC` tables for you to find these values due to the lack of `glyf`/`loca` tables).

### Microsoft's `COLR`/`CPAL` format

Microsoft's `COLR`/`CPAL` format is currently not supported.

## Injecting Images into Fonts to Create Emoji/Color Fonts

To create an emoji/color font, you will need two things:
  *  A plain black-and-white TrueType font into which you intend to inject the color images.
  *  The accompanying directory of images, in the same structure and format as generated by the extraction process above.

### Example using Apple's `sbix` format

`java -jar BitsNPicas.jar injectsbix MyEmoji.ttf`

This will take the TrueType font file `MyEmoji.ttf` and the images in the directory `MyEmoji.ttf.sbix.d` and generate a new TrueType font file `MyEmoji.ttf.sbix.ttf` with images embedded in an `sbix` table. You can then rename this file if you like. The name of each subdirectory within the image directory must be the *pixels per em* of the images it contains. (This is not necessarily the same thing as the image height!) The file name of each image within each subdirectory must be of the form `glyph_123.png` (with the glyph index in decimal) or `char_ABCD.png` (with the Unicode code point in hexadecimal). The bottom left position of each image will be determined by the minimum X and Y coordinates of the corresponding glyph in the TrueType font file. (For Apple Color Emoji, this just happens to be set to 0,0.)

### Example using Adobe and Mozilla's SVG-in-OpenType format

`java -jar BitsNPicas.jar injectsvg MyEmoji.otf`

This will take the OpenType font file `MyEmoji.otf` and the SVG images in the directory `MyEmoji.otf.svg.d` and generate a new OpenType font file `MyEmoji.otf.svg.otf` with images embedded in an `SVG ` table. You can then rename this file if you like. The file name of each image within the image directory must be of the form `glyph_123_456.svg` (for a range of glyphs, with the glyph indices in decimal), `glyph_123.svg` (for a single glyph, with the glyph index in decimal), or `char_ABCD.svg` (for a single glyph, with the Unicode code point in hexadecimal).

For each glyph index mapped to an SVG document, there must be an element in that document with an id corresponding to that glyph index (e.g. `id="glyph123"` for glyph index 123). Since you may not know the glyph index when specifying a code point in the image file name, you can use the placeholder `id="glyph{{{0}}}"` and it will be replaced with the appropriate glyph index during the injection process.

If you specify the `-z` option before the font file name, the SVG data will be compressed using GZIP. Microsoft claims the SVG-in-OpenType format supports this, and the availability of a version of EmojiOne with GZIP-compressed SVG glyphs appears to back this up. However, I was unable to get fonts with GZIP-compressed SVG glyphs to work even in the latest version of Firefox as of this writing, so I do not recommend this.

If you have a bunch of non-SVG images you would like to inject using this format, you can easily generate SVGs with embedded images using a command like the following:

`java -jar BitsNPicas.jar imagetosvg -x 0 -y -700 -w 800 -h 800 myimages/*.png`

This will create a corresponding SVG file for each non-SVG image file specified. The `-x`, `-y`, `-w`, `-h` options specify the position and size of the image in em units. Note that the SVG Y-axis is reversed from the TrueType Y-axis, so negative Y-coordinates are *above* the baseline and positive Y-coordinates are *below* the baseline.

### Example using Google's `CBDT`/`CBLC` format

`java -jar BitsNPicas.jar injectcbdt MyEmoji.ttf`

This will take the TrueType font file `MyEmoji.ttf` and the PNG images in the directory `MyEmoji.ttf.cbdt.d` and generate a new TrueType font file `MyEmoji.ttf.cbdt.ttf` with images embedded in a `CBDT` table. You can then rename this file if you like. There must be a subdirectory for each bitmap size; the names of the subdirectories do not matter. The file name of each image within each subdirectory must be of the form `glyph_123.png` (with the glyph index in decimal) or `char_ABCD.png` (with the Unicode code point in hexadecimal).

It is recommended for each subdirectory to also contain a `metadata.txt` file of values to use for the various data structures within the `CBLC` and `CBDT` tables. The following is recommended at a minimum:

```
  horiAscender: *ascent in pixels (normally positive)*
  horiDescender: *descent in pixels (normally negative)*
  horiWidthMax: *max character width in pixels*
  vertAscender: *ascent in pixels (normally positive)*
  vertDescender: *descent in pixels (normally negative)*
  vertWidthMax: *max character width in pixels*
  ppemX: *pixels per em*
  ppemY: *pixels per em*
  
  glyph: *glyph index*
  height: *image height in pixels*
  width: *image width in pixels*
  bearingX: *x offset in pixels*
  bearingY: *y offset in pixels (positive is above the baseline)*
  advance: *character width in pixels*
  endGlyph
```

For the glyph index you can also use `char_ABCD` (with the Unicode code point in hexadecimal) or `*` to cover all glyphs not otherwise specified. Fields associated with the data structure itself (various offsets, counts, and glyph index ranges, as well as anything associated with the various types of index subtables) do not need to be specified, will be ignored if specified, and will be calculated automatically. (Only index subtable format 1 and bitmap data formats 17, 18, and 19 will be used, regardless of the metadata specified.)

By default, `injectcbdt` will also remove any `glyf`, `loca`, `CFF `, or `CFF2` tables from the font. To keep these tables, specify the `-g` option before the font file name.

### Microsoft's `COLR`/`CPAL` format

Microsoft's `COLR`/`CPAL` format is currently not supported.
