#version 330 core

uniform vec4 color;
uniform vec4 borderColor;
uniform vec4 border; // top bottom left right

in vec2 UV;
out vec4 outColor;

vec4 applyBorderColor(vec2 centerPoint, float radius, vec2 borderThickness) {
    float dist = distance(UV, centerPoint);
    float borderMult = distance(vec2(radius, 0), centerPoint);
    float borderCalc = borderThickness.x * borderMult + ((1 - borderMult) * borderThickness.y);
    float mult = 1 - (abs(dist - radius) / 0.02);

//    if (dist - borderCalc > radius) return vec4(0);
    if (dist > radius) return mix(vec4(0), borderColor, mult);
    else return mix(color, borderColor, mult);
}

void main() {
    float radius = 0.4;

    // handle border radius
    if (UV.x < radius && UV.y < radius) outColor = applyBorderColor(vec2(radius, radius), radius, border.xz);
    else if (1 - UV.x < radius && UV.y < radius) outColor = applyBorderColor(vec2(1 - radius, radius), radius, border.xw);
    else if (UV.x < radius && 1 - UV.y < radius) outColor = applyBorderColor(vec2(radius, 1 - radius), radius, border.yz);
    else if (1 - UV.x < radius && 1 - UV.y < radius) outColor = applyBorderColor(vec2(1 - radius, 1 - radius), radius, border.yw);
    else {
        float predist = min(min(min(UV.x, UV.y), 1 - UV.x), 1 - UV.y);
        float dist = abs(radius - predist);
        float mult = 1 - (abs(dist - radius) / 0.02);

        // draw border
        if (UV.y < border.x || UV.x < border.z || 1 - UV.y < border.y || 1 - UV.x < border.w) {
            outColor = mix(color, borderColor, mult);
            return;
        }

        // send default if we made it this far
        outColor = color;
    }
//    outColor = vec4(UV, 0, 1);
}