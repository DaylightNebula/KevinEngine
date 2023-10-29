#version 330 core
layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec2 vertexUV;

const int MAX_BONES = 100;

out vec2 UV;

uniform mat4 mvp;

void main() {
    // calculate final position with bone matrices
//    vec4 finalPosition = vec4(0.0f);
//    for (int i = 0; i < 4; i++) {
//        // get bone id and verify it is within bounds
//        int boneID = boneIDs[i];
//        if (boneID == -1) continue;
//        else if (boneID >= MAX_BONES) {
//            finalPosition = vec4(vertexPosition_modelspace, 1.0f);
//            break;
//        }
//
//        // calculate position influence by this bone and its weight and add to the final position
//        finalPosition += bones[boneID] * vec4(vertexPosition_modelspace, 1.0f) * boneWeights[i];
//    }
    gl_Position = mvp * vec4(vertexPosition_modelspace, 1.0f);

    UV = vertexUV;
}
