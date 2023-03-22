/*-
 * #%L
 * eTarget Maven Webapp
 * %%
 * Copyright (C) 2017 - 2021 digital ECMT
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
// Logout if idle for 15 minutes
var timeoutInSeconds = 840;
var timeoutInMiliseconds = 840000;
var countdown;
var timeoutId;
var timer;

function startTimer() {
    // window.setTimeout returns an Id that can be used to start and stop a timer
    timeoutId = window.setTimeout(doInactiveIE, timeoutInMiliseconds);
}

function doInactiveIE() {
    $('body').prepend('<div style="width:98%;padding:1%;border-bottom:5px solid #ff1a1a;border-top:5px solid #ff1a1a;font-size:20px;text-align:center;" id="timeout-message">You will be logged out in <span style="font-size:20px" id="countdown-timer">60</span></div>');

    var countdown = 60;
    timer = setInterval(function() {
      countdown = countdown-1;
      $('#countdown-timer').text(countdown);

      if(countdown <= 1) {
        window.location.href = "./logout.jsp";
        countdown = 0;
      }
    }, 1000);
}

function setupTimers() {
    document.addEventListener("mousemove", resetTimer, false);
    document.addEventListener("mousedown", resetTimer, false);
    document.addEventListener("keypress", resetTimer, false);
    document.addEventListener("touchmove", resetTimer, false);
}



function resetTimer() {
	countdown=timeoutInSeconds;
	$('#timeout-message').remove();
    window.clearInterval(timer);
    window.clearTimeout(timeoutId);
    startTimer();
}

function doInactive(countdown) {
	if($('#timeout-message').length==0){
    	$('body').prepend('<div style="width:98%;padding:1%;border-bottom:5px solid #ff1a1a;border-top:5px solid #ff1a1a;font-size:20px;text-align:center;" id="timeout-message">You will be logged out in <span style="font-size:20px" id="countdown-timer">60</span></div>');
    }
    else{
    	$('#timeout-message').replaceWith('<div style="width:98%;padding:1%;border-bottom:5px solid #ff1a1a;border-top:5px solid #ff1a1a;font-size:20px;text-align:center;" id="timeout-message">You will be logged out in <span style="font-size:20px" id="countdown-timer">60</span></div>');
    }

     $('#countdown-timer').text(countdown);

     if(countdown <= 1) {
        window.location.href = "./logout.jsp";
        countdown = 0;
     }
}


var worker;
countdown=timeoutInSeconds;
if (typeof(Worker) !== "undefined") {
    if (typeof(worker) == "undefined") {
      	worker = new Worker("js/timeoutworker.js");
		worker.onmessage = function (event) {
			countdown-=1;
			if(countdown<=60){
				doInactive(countdown);
			}			
		}
		worker.onerror = function (event) {
			console.log(event);
		}
      
    }

  } else {
  //need to do the old stuff
    startTimer();
  }
setupTimers();


