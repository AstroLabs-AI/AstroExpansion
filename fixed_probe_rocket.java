// This is a simplified version of the fix for the Probe Rocket model
// Copy these adjustments into the appropriate section of RocketRenderer.java

// For the Probe Rocket model configuration:

// Main cylindrical body - ensure all parts visually overlap
this.body.visible = true;
this.body.xScale = 0.6F; // Smaller for probe
this.body.yScale = 2.0F; // Significantly extended to ensure overlap with nose and nozzle
this.body.zScale = 0.6F; // Smaller for probe
this.body.y = 0F; // Origin for our coordinate system

// Technical components as nose - ensure significant overlap with body
this.nose.visible = true;
this.nose.y = -3F; // Positioned to overlap with the body instead of just touching
this.nose.xScale = 0.5F; // Smaller for probe
this.nose.yScale = 1.2F; // Extended to ensure significant overlap with body
this.nose.zScale = 0.5F; // Smaller for probe

// Small engine nozzle - ensure overlap with body
this.nozzle.visible = true;
this.nozzle.xScale = 0.8F;
this.nozzle.yScale = 1.5F; // Extended to ensure significant overlap with body
this.nozzle.zScale = 0.8F;
this.nozzle.y = 3.0F; // Positioned to overlap with the body instead of just touching

// Use details as tech components/antennas
if (this.detailPart1 != null) {
    this.detailPart1.visible = true;
    this.detailPart1.y = -5F; // Position antenna to overlap with nose for continuity
    this.detailPart1.xScale = 0.4F;
    this.detailPart1.yScale = 0.8F;
    this.detailPart1.zScale = 0.4F;
}
