#version 330 core

uniform vec4 color;
uniform vec4 borderColor;
uniform vec4 border; // top bottom left right
uniform vec4 borderRadius; // top-left top-right bottom-left bottom-right
uniform float borderFactor;

in vec2 UV;
out vec4 outColor;

vec4 applyBorderColor(vec2 centerPoint, float radius, vec2 borderThickness) {
    float dist = distance(UV, centerPoint);
    float borderMult = distance(vec2(radius, 0), centerPoint);
    float borderCalc = borderThickness.x * borderMult + ((1 - borderMult) * borderThickness.y);
    float mult = 1 - (abs(dist - radius) / borderFactor);

//    if (dist - borderCalc > radius) return vec4(0);
    if (dist > radius) return mix(vec4(0), borderColor, mult);
    else return mix(color, borderColor, mult);
}

void main() {
//    float radius = 0.4;

    // handle border radius
    if (UV.x < borderRadius.x && UV.y < borderRadius.x) outColor = applyBorderColor(vec2(borderRadius.x + 0.02, borderRadius.x + 0.02), borderRadius.x, border.xz);
    else if (1 - UV.x < borderRadius.y && UV.y < borderRadius.y) outColor = applyBorderColor(vec2(1 - borderRadius.y - 0.02, borderRadius.y + 0.02), borderRadius.y, border.xw);
    else if (UV.x < borderRadius.z && 1 - UV.y < borderRadius.z) outColor = applyBorderColor(vec2(borderRadius.z + 0.02, 1 - borderRadius.z - 0.02), borderRadius.z, border.yz);
    else if (1 - UV.x < borderRadius.w && 1 - UV.y < borderRadius.w) outColor = applyBorderColor(vec2(1 - borderRadius.w - 0.02, 1 - borderRadius.w - 0.02), borderRadius.w, border.yw);
    else {
        float predist = min(min(min(UV.x, UV.y), 1 - UV.x), 1 - UV.y);
        float borderDist = abs(predist - borderFactor);
//        outColor = mix(borderColor, color, borderDist / 0.02);

        if (predist > borderDist) outColor = mix(borderColor, color, borderDist / borderFactor);
        else outColor = mix(borderColor, vec4(0), borderDist / borderFactor);
        return;

        // draw border
        if (UV.y < border.x || UV.x < border.z || 1 - UV.y < border.y || 1 - UV.x < border.w) {
            outColor = vec4(vec3(predist), 1);
//            outColor = mix(color, borderColor, mult);
//            outColor = borderColor;
            return;
        }

        // send default if we made it this far
        outColor = color;
    }
//    outColor = vec4(UV, 0, 1);
}