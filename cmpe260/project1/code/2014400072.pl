% % % % %
%
% 2017
% CMPE260 PROLOG PROJECT
% Cemal Burak AYGÜN
% 2014400072
%
% % % % %



:- dynamic
    
    student/2,
    available_slots/1,
    room_capacity/2.



% % % % %
% clear_knowledge_base/0
%
% Uses these built-in predicates: findall/3 , length/2 , retractall/1 , write/1 and writeln/1
%
% findall/3 inserts '1' as an element into the list 'ListFor_<fact>_Number' for each <fact> it finds in the knowledge base.
% At the end, the length of 'ListFor_<fact>_Number' becomes equal to the number of <fact>s in the knowledge base.
%
% ( Replace <fact> above with student , available_slots or room_capacity. )
% % % % %
clear_knowledge_base :-
    
    findall( 1 , student(_,_) , ListFor_student_Number ),
    length( ListFor_student_Number , NumberOf_student ),
    retractall( student(_ , _) ),
    write("students/2: "),
    writeln(NumberOf_student),
    
    findall( 1 , available_slots(_) , ListFor_available_slots_Number ),
    length( ListFor_available_slots_Number , NumberOf_available_slots ),
    retractall( available_slots(_) ),
    write("available_slots/1: "),
    writeln(NumberOf_available_slots),

    findall( 1 , room_capacity(_,_) , ListFor_room_capacity_Number ),
    length( ListFor_room_capacity_Number , NumberOf_room_capacity ),
    retractall( room_capacity(_,_) ),
    write("room_capacity/2: "),
    writeln(NumberOf_room_capacity).



% % % % %
% all_students/1
%
% Uses these built-in predicates: findall/3
% % % % %
all_students( StudentList ) :-
    
    findall( StudentID , student( StudentID , _ ) , StudentList ).



% % % % %
% all_courses/1
%
% Uses these built-in predicates: setof/3 and member/2
%
% setof/3 goes through student/2 facts and finds lists of courses ('ListOfCourses').
% Then, using member/2 gets members ('CourseID') of 'ListOfCourses' and inserts them into 'CourseList' discarding duplicate 'CourseID's.
% % % % %
all_courses( CourseList ) :-
    
    setof( CourseID , StudentID^ListOfCourses^ ( student(StudentID,ListOfCourses) , member(CourseID,ListOfCourses) ) , CourseList ).



% % % % %
% student_count/2
%
% Uses these built-in predicates: findall/3 , memberchk/2 and length/2
%
% findall/3 goes through student/2 facts and finds lists of courses ('ListOfCourses').
% Then, using memberchk/2 checks whether given 'CourseID' is a member of 'ListOfCourses' (means the student takes course 'CourseID')
% and if so, inserts '1' as an element into the list 'ListForStudentCount'.
% At the end, the length of 'ListForStudentCount' becomes equal to 
% the number of students who take course 'CourseID'.
% % % % %
student_count( CourseID , StudentCount ) :-
    
    findall( 1 , ( student(_,ListOfCourses) , memberchk(CourseID,ListOfCourses) ) , ListForStudentCount ),
    
    length( ListForStudentCount , StudentCount ).



% % % % %
% common_students/3
%
% Uses these built-in predicates: findall/3 , memberchk/2 and length/2
%
% findall/3 goes through student/2 facts and finds lists of courses ('ListOfCourses').
% Then, using memberchk/2 checks whether given 'CourseID1' and 'CourseID2' are members of 'ListOfCourses'
% (means the student takes both courses 'CourseID1' and 'CourseID2')
% and if so, inserts '1' as an element into the list 'ListForStudentCount'.
% At the end, the length of 'ListForStudentCount' becomes equal to 
% the number of students who take both of courses 'CourseID1' and 'CourseID2'.
% % % % %
common_students( CourseID1 , CourseID2 , StudentCount ) :-
    
    findall( 1 , ( student(_,ListOfCourses) , memberchk(CourseID1,ListOfCourses) , memberchk(CourseID2,ListOfCourses) ) , ListForStudentCount ),
    
    length( ListForStudentCount , StudentCount ).



% % % % %
% final_plan/1
%
% Uses these implemented predicates: all_courses/1 and recursive_final_plan/3
%
% This predicate works as the driver of recursive_final_plan/3.
% % % % %
final_plan( FinalPlan ) :-
    
    all_courses(AllCourses),        % Gets the list of courses
    
    recursive_final_plan( AllCourses , [] , FinalPlan ).    % and sends it to recursive_final_plan/3 as the first argument.



% % % % %
% recursive_final_plan/3
%
% This is the base.
% When the list in the first argument becomes the empty list,
% 'Result' becomes equal to 'AccumulatorList' (see next predicate)
% % % % %
recursive_final_plan( [] , Result , Result ) :-
    !.      % Cuts the other choices of recursive_final_plan/3.



% % % % %
% recursive_final_plan/3
%
% Uses these built-in predicates: member/2 , (\+)/2 , memberchk/2 and append/3
% Uses these implemented predicates: student_count/2 , find_time_slot_conflict/3 and recursive_final_plan/3
%
% The first argument is a list of courses. (Ex: [math102 , ee212 , phys201 , ... ])
% The second argument is a list (initially empty, its members will be 3-membered lists) that is used as an accumulator to find
% the third argument 'Result'.
% Both 'AccumulatorList' and 'Result' are in the form of [ [Course1,Room1,TimeSlot1] , [Course2,Room2,TimeSlot2] , ... ].
%
% This predicate takes the head (which is a course ID such as math102) of its first argument and tries to find an appropriate exam slot for it.
% If such an exam slot is found, it stores that information as an element in the 'AccumulatorList' and
% tries to find appropriate exam slots for the remaining elements (remaining courses) of its first argument.
% It continues until the list in the first argument becomes the empty list. (And in this case, 'Result' becomes equal to 'AccumulatorList')
% % % % %
recursive_final_plan( [CourseID | ListOfOtherCourses] , AccumulatorList , Result ) :-
    
    student_count( CourseID , StudentCount ),   % Finds the number of students who take course 'CourseID'.
    
    room_capacity( RoomID , RoomCapacity ),     % Finds a room for the exam.
    
    RoomCapacity >= StudentCount,               % If the capacity of the room is equal to or greater than the number of students who take course 'CourseID', contiunes. Otherwise, goes to the previous step and looks for another room.
    
    available_slots( ListOfTimeSlots ),          % Gets the list of available time slots.
    
    member( TimeSlot , ListOfTimeSlots ),        % Gets a time slot from the list of available time slots.
    
    \+ memberchk( [_ , RoomID , TimeSlot] , AccumulatorList ),  % Checks whether the exam slot with 'TimeSlot' and 'RoomID' was assigned to a course before. If so, goes back and looks for another 'TimeSlot' and/or 'RoomID'. Otherwise, continues.
    
    \+ find_time_slot_conflict( AccumulatorList , CourseID , TimeSlot ),    % Checks whether there will be any students who have 2 exams in the same 'TimeSlot' if it assigns 'TimeSlot' to 'CourseID'. If so, goes back and looks for another 'TimeSlot'. Otherwise, continues.
    
    append( AccumulatorList , [ [CourseID , RoomID , TimeSlot] ] , NewAccumulatorList ), % Found an appropriate exam slot for 'CourseID'. Stores this information in 'AccumulatorList' as an element which is a 3-membered list.
    
    recursive_final_plan( ListOfOtherCourses , NewAccumulatorList , Result ).   % Done with 'CourseID'. Looks for appropriate exam slots for the remaining courses.



% % % % %
% find_time_slot_conflict/3
%
% Uses these built-in predicates: member/2
% Uses these implemented predicates: common_students/3
%
% The first argument is a list whose members are 3-membered lists.
% This list is actually a partially formed final plan in the form of [ [Course1,Room1,TimeSlot1] , [Course2,Room2,TimeSlot2] , ... ].
% The second argument is a course ID. (Ex: math102)
% The third argument is a time slot (Ex: 'w-2') which is intended to be assigned to 'CourseID1'.
%
% This predicate checks whether assigning 'TimeSlot' to 'CourseID1' will cause a "problem" according to
% the so-far-formed final plan ('ListOfPartialFinalPlan').
% The "problem" is defined as follows: There are some students who will have two exams in the same 'TimeSlot'.
% % % % %
find_time_slot_conflict( ListOfPartialFinalPlan , CourseID1 , TimeSlot ) :-
    
    member( [CourseID2 , _ , TimeSlot] , ListOfPartialFinalPlan ),  % Tries to find an exam that was assigned to 'TimeSlot' and learns the corresponding course ('CourseID2').
    
    common_students( CourseID1 , CourseID2 , CommonStudents ),      % Gets the number of students who take both 'CourseID1' and 'CourseID2'.
    
    CommonStudents =\= 0.     % If the number of the students who take both 'CourseID1' and 'CourseID2' is not equal to 0, it means that there are some students who will have 2 exams in the same 'TimeSlot'. In this case, the predicate gives true. Otherwise, goes 2 steps back and tries to find another exam that was assigned to 'TimeSlot'.



% % % % %
% errors_for_plan/2
%
% Uses these built-in predicates: findall/3 , bagof/3 , member/2 , length/2 and is/2
% Uses these implemented predicates: error_for_room_capacity/3 and error_for_time_slot_phase_1/3
%
% This predicate works as the drivers of error_for_room_capacity/3 and error_for_time_slot_phase_1/3 predicates.
% % % % %
errors_for_plan( FinalPlan , ErrorCount ) :-

    error_for_room_capacity( FinalPlan , 0 , ErrorForRoomCapacity ),    % Gets the overall "error count" which is resulted from that the number of attendees of a course is greater than the capacity of the assigned room for that course.
    
    
    
    % findall/3 below is used to get a list for the first argument of error_for_time_slot_phase_1/3 below.
    % findall/3 finds all values of 'ListOfCoursesWithSameTimeSlot' bagof/3 produces and if its length is greater than 1, inserts it into 'ListOfListsOfCoursesWithSameTimeSlot'.
    % At the end, findall/3 produces a list whose members are at-least-2-membered lists and those members are different courses that were assigned to the same time slot.
    % So, 'ListOfListsOfCoursesWithSameTimeSlot' is in the form of [ [Course(1) , ... , Course(m) ] , [Course(1) , ... ,Course,(n)] , ... ].
    findall( 
                ListOfCoursesWithSameTimeSlot ,     % The first argument (Template) of findall/3
                
                (   % The second argument (Goal) of findall/3 starts here
                    
                    % bagof/3 below works as follows:
                    % The second argument of member/2 is a final plan
                    % in the form of [ [Course1,Room1,TimeSlot1] , [Course2,Room2,TimeSlot2]  , ... ].
                    % So, the character '_' in the first argument of member/2 corresponds to a time slot from 'FinalPlan'.
                    % bagof/3 basically goes through 'FinalPlan'
                    % and groups courses according to the time slots only (because 'RoomID' is ignored with ^) and 
                    % gives a list of courses for each time slot value in 'FinalPlan'.                        
                    bagof( CourseID , RoomID^ member( [CourseID , RoomID , _] , FinalPlan ) , ListOfCoursesWithSameTimeSlot ) , 
                    
                    length( ListOfCoursesWithSameTimeSlot , LengthOfListOfCoursesWithSameTimeSlot ) ,   % Finds the length of 'ListOfCoursesWithSameTimeSlot' bagof/3 produces above and
                    
                    ( LengthOfListOfCoursesWithSameTimeSlot > 1 )     %  if the length is greater than 1, inserts the list into 'ListOfListsOfCoursesWithSameTimeSlot'. Otherwise, goes back to bagof/3 for another 'ListOfCoursesWithSameTimeSlot'.
                
                ) , % The second argument (Goal) of findall/3 ends here
                
                ListOfListsOfCoursesWithSameTimeSlot    % The third argument (Bag) of findall/3
           ),
   
    error_for_time_slot_phase_1( ListOfListsOfCoursesWithSameTimeSlot , 0 , ErrorForTimeSlot ),     % Gets the overall "error count" which is resulted from that there are some students who have at least 2 exams in the same time slot.
    
    ErrorCount is ( ErrorForRoomCapacity + ErrorForTimeSlot ).



% % % % %
% error_for_room_capacity/3
%
% Uses these built-in predicates: !/0
%
% This is the base.
% When the list in the first argument becomes the empty list,
% 'Result' becomes equal to 'Accumulator' (see next predicate)
% % % % %
error_for_room_capacity( [] , Result , Result ) :-
    !.      % Cuts the other choices of error_for_room_capacity/3.



% % % % %
% error_for_room_capacity/3
% 
% Uses these built-in predicates: !/0 and is/2
% Uses these implemented predicates: student_count/2 and error_for_room_capacity/3
%
% The first argument is a list of 3-membered lists in the form of [ [Course1,Room1,TimeSlot1] , [Course2,Room2,TimeSlot2] , ... ].
% The second argument is a variable that is used as an accumulator to find
% the third argument 'Result'.
%
% This predicate recursively goes through the final plan (its first argument) and
% calculates the overall "error count" resulted from that
% the number of attendees of a course is greater than the capacity of the assigned room for that course.
% % % % %
error_for_room_capacity( [ [CourseID , RoomID , _] | TailOfFinalPlan ] , Accumulator , Result) :-
    
    student_count( CourseID , StudentCount ),   % Finds the number of students who take course 'CourseID'.
    
    room_capacity( RoomID , RoomCapacity ),     % Finds the capacity of the room that was assigned to 'CourseID'.
    
    StudentCount > RoomCapacity,                % If the number of the attendees is greater than the capacity of the room, continues. Otherwise, goes to other choice of error_for_room_capacity/3. 
    
    !,      % Cuts the other choices of error_for_room_capacity/3.
    
    NewAccumulator is ( Accumulator + (StudentCount - RoomCapacity) ),      % Calculates the "error count" corresponding to 'CourseID' and 'RoomID' and adds it to 'Accumulator'.
    
    error_for_room_capacity( TailOfFinalPlan , NewAccumulator , Result ).   % Done with 'CourseID'. Looks for "error count"s for the remaining final plan.



% % % % %
% error_for_room_capacity/3
%
% Uses these implemented predicates: error_for_room_capacity/3
%
% The first argument is a list of 3-membered lists in the form of [ [Course1,Room1,TimeSlot1] , [Course2,Room2,TimeSlot2] , ... ].
% The second argument is a variable that is used as an accumulator to find
% the third argument 'Result'.
%
% This predicate works only if the condition "StudentCount > RoomCapacity" in error_for_room_capacity/3 above is false.
% It basically discards the head and continues the recursion with the tail of the list in its first argument.
% % % % %
error_for_room_capacity( [_ | TailOfFinalPlan] , Accumulator , Result ) :-
    
    error_for_room_capacity( TailOfFinalPlan , Accumulator , Result ).



% % % % %
%
% EXPLANATION OF HOW error_for_time_slot_phase_<i>/3 (i = 1 , 2 or 3) BELOW WORK TOGETHER
%
% % % % %
%
% Phase1 takes "a list of at-least-2-membered lists of courses" as its first argument.
% For example [ [Course1a , Course1b , Course1c] , [Course2a , Course2b] , [Course3a , Course3b , Course3c , Course3d] ... ]
% 
% STEP 1: Phase1 sends the head of its first argument to Phase2.
% So, Phase2 gets list "[Course1a , Course1b , Course1c]" in its first argument.
%
% STEP 2: Phase2 splits its first argument into its head and tail. Then, sends them to Phase3.
% So, Phase3 gets "Course1a" in its first argument and list "[Course1b , Course1c]" in its second argument.
%
% STEP 3: Phase3 finds the number of common students for "Course1a" and "Course1b" (say, num1) and
% the number of common students for "Course1a" and "Course1c" (say, num2) and gives the result of (num1+num2) (say, res1) to Phase2.
%
% STEP 4: Phase2 adds res1 to its "Accumulator" and calls itself recursively.
% This time Phase3 gets "Course1b" in its first argument and list "[Course1c]" in its second argument.
%
% STEP 5: Phase3 finds the number of common students for "Course1b" and "Course1c" (say, res2) and gives it to Phase2.
%
% STEP 6: Phase2 adds res2 to its "Accumulator" (now its value is res1+res2 = RESUL1) and give RESULT1 to Phase1.
%
% Then, steps 1 to 6 are done for "[Course2a , Course2b]". (Phase1 adds RESULT2 to its "Accumulator".)
% Then, for "[Course3a , Course3b , Course3c , Course3d]". (Phase1 adds RESULT3 to its "Accumulator".)
% ...
% Then, for the last element (say n) of the list in the first argument of Phase1.    (Phase1 adds RESULT(n) to its "Accumulator".)
%
% At the end Phase1 gives the result of RESULT1 + RESULT2 + .... + RESULT(n)
%
% % % % %



% % % % %
% error_for_time_slot_phase_1/3
%
% This is the base.
%
% When the list in the first argument becomes the empty list,
% 'Result' becomes equal to 'Accumulator'. (see next predicate)
% % % % %
error_for_time_slot_phase_1( [] , Result , Result ) :-
    
    !.      % Cuts the other choices of error_for_time_slot_phase_1/3.



% % % % %
% error_for_time_slot_phase_1/3
%
% Uses these built-in predicates: is/2
% Uses these implemented predicates: error_for_time_slot_phase_2/3 and error_for_time_slot_phase_1/3
%
% The first argument is a list of at-least-2-membered lists in the form of [ [Course(1) , ... , Course(m)] , [Course(1) , ... ,Course,(n)] , ... ].
% Each member of the outer list represents a different time slot and for each inner list the members of the list are different from each other.
% The second argument is a variable that is used as an accumulator to find
% the third argument 'Result'.
%
% This predicate recursively sends the members of its first argument to error_for_time_slot_phase_2/3, one member at each iteration.
% At the end, 'Result' becomes equal to the overall "error count" resulted from that
% there are some students who have at least 2 exams in the same time slot.
% % % % %
error_for_time_slot_phase_1( [ListOfCoursesWithSameTimeSlot | OtherListsOfCoursesWithSameTimeSlot] , Accumulator , Result ) :-

    error_for_time_slot_phase_2( ListOfCoursesWithSameTimeSlot , 0 , ResultFromPhase2 ),    % Gets the partial "error count" for the courses in 'ListOfCoursesWithSameTimeSlot'.
    
    NewAccumulator is ( Accumulator + ResultFromPhase2 ),       % Adds partial "error count" to 'Accumulator'.
    
    error_for_time_slot_phase_1( OtherListsOfCoursesWithSameTimeSlot , NewAccumulator , Result ).   % Done with 'ListOfCoursesWithSameTimeSlot'. goes for the tail of the first argument.



% % % % %
% error_for_time_slot_phase_2/3
%
% This is the base.
%
% When the list in the first argument becomes a single-membered list,
% 'Result' becomes equal to 'Accumulator'. (see next predicate)
% % % % %
error_for_time_slot_phase_2( [_] , Result , Result ) :-
    
    !.  % Cuts the other choices of error_for_time_slot_phase_2/3.



% % % % %
% error_for_time_slot_phase_2/3
%
% Uses these built-in predicates: is/2
% Uses these implemented predicates: error_for_time_slot_phase_3/3 and error_for_time_slot_phase_2/3
%
% The first argument is a list of courses that were assigned to the same time slot.
% The second argument is a variable that is used as an accumulator to find
% the third argument 'Result'.
%
% This predicate splits its first argument into its head and tail and sends them to error_for_time_slot_phase_3/3. 
% At every iteration, it considers all of the combinations of the head ('CourseID') with a member from the tail ('ListOfOtherCourses')
% and adds the number of students who take both courses to 'Accumulator'.
% % % % %
error_for_time_slot_phase_2( [CourseID | ListOfOtherCourses] , Accumulator , Result ):-
    
    error_for_time_slot_phase_3( CourseID , ListOfOtherCourses , 0 , ResultFromPhase3 ),
    
    NewAccumulator is ( Accumulator + ResultFromPhase3 ),
    
    error_for_time_slot_phase_2( ListOfOtherCourses , NewAccumulator , Result ).    % Done with 'CourseID'.



% % % % %
% error_for_time_slot_phase_3/3
%
% This is the base.
%
% When the list in the second argument becomes the empty list,
% 'Result' becomes equal to 'Accumulator'. (see next predicate)
% % % % %
error_for_time_slot_phase_3( _ , [] , Res , Res ) :-
    
    !.  % Cuts the other choices of error_for_time_slot_phase_3/3.



% % % % %
% error_for_time_slot_phase_3/3
%
% Uses these built-in predicates: is/2
% Uses these implemented predicates: common_students/3 and error_for_time_slot_phase_3/3
%
% The first argument is a course ID.
% The second argument is a list of courses that were assigned to the same time slot as the first argument ('Course1').
% The third argument is a variable that is used as an accumulator to find
% the forth argument 'Result'.
%
% This predicate compares its first argument ('Course1') with each member (say "Course(n)") of its second argument and
% adds the number of students who take both 'Course1' and "Course(n)" to 'Accumulator'.
% At the end, 'Result' becomes equal to the total number of students who take both 'Course1' and "Course(n)"
% % % % %
error_for_time_slot_phase_3( Course1 , [Course2 | ListOfOtherCourses] , Accumulator , Result ) :-
        
    common_students( Course1 , Course2 , StudentCount ),    % Gets the number of students who take both 'Course1' and 'Course2'.
    
    NewAccumulator is ( Accumulator + StudentCount ),
    
    error_for_time_slot_phase_3( Course1 , ListOfOtherCourses , NewAccumulator , Result ).  % Done with 'Course2'.
