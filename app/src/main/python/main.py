import base64
import cv2
import numpy as np
import io
from PIL import Image

count = None
pill_number = None

# Set default preferences to White and No Numerical Indicator
redAmt = 255
greenAmt = 255
blueAmt = 255
numBool = False

def main(data):
    global count, pill_number
    # Decodes data from Android Studio
    data = base64.b64decode(data)
    # Convert Data to np Data
    temp_data = np.fromstring(data, np.uint8)
    # Converts Data back to Image
    image = cv2.imdecode(temp_data, cv2.IMREAD_UNCHANGED)
    # Resizes Image to 640 by 800
    image = cv2.resize(image, (640, 800))
    # Converts Image to Gray
    gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    # Blurs Image to Find Contours Easier. Kernel size (7,7). Center Division = 3
    blur_image = cv2.GaussianBlur(gray_image, (7, 7), 3)
    # Detects Edges
    canny_image = cv2.Canny(blur_image, 30, 150, 3)
    # Thickens Edges
    dilated_image = cv2.dilate(canny_image, (1, 1), iterations=3)
    # Finds contours
    (cnt, hierarchy) = cv2.findContours(dilated_image.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    # Converts back to color
    rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    # Draws edges on color image
    cv2.drawContours(rgb_image, cnt, -1, (redAmt, greenAmt, blueAmt), 3) # color change
    pill_number = 1

    # numerical indicator section
    for c in cnt:
            # Calculate the center of the contour
        M = cv2.moments(c)
        if M["m00"] != 0:
            cX = int(M["m10"] / M["m00"])
            cY = int(M["m01"] / M["m00"])

                # Draw the numeral indicator
            if(numBool):
                cv2.putText(rgb_image, str(pill_number), (cX, cY), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
            pill_number += 1



    pil_image = Image.fromarray(rgb_image)
    buff = io.BytesIO()
    pil_image.save(buff, format='JPEG')
    image_string = base64.b64encode(buff.getvalue()).decode('utf-8')
    count = len(cnt)
    return image_string

def getCount():
     global count,pill_number
     return pill_number


def setPreference(red, green, blue, isNumbered):
    global redAmt, greenAmt, blueAmt, numBool
    redAmt = red
    blueAmt = blue
    greenAmt = green
    numBool = isNumbered